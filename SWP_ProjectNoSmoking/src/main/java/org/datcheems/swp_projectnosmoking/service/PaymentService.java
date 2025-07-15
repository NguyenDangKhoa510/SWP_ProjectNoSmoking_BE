package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.config.VNPayConfig;
import org.datcheems.swp_projectnosmoking.dto.request.UserMembershipRequest;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MembershipPackage;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.MembershipPackageRepository;
import org.datcheems.swp_projectnosmoking.utils.VNPayUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final VNPayConfig vnPayConfig;
    private final UserMembershipService userMembershipService;
    private final MemberRepository memberRepository;
    private final MembershipPackageRepository membershipPackageRepository;

    public boolean verifyPayment(Map<String, String> params) {
        try {
            // Tạo copy để không modify params gốc
            Map<String, String> verifyParams = new HashMap<>(params);

            String secureHash = verifyParams.get("vnp_SecureHash");
            if (secureHash == null) {
                log.error("Missing vnp_SecureHash");
                return false;
            }

            // Remove hash từ params để verify
            verifyParams.remove("vnp_SecureHash");
            verifyParams.remove("vnp_SecureHashType");

            // Sắp xếp theo alphabet
            List<String> fieldNames = new ArrayList<>(verifyParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = verifyParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // URL decode nếu cần
                    try {
                        String decodedValue = java.net.URLDecoder.decode(fieldValue, "UTF-8");
                        hashData.append(fieldName).append('=').append(decodedValue).append('&');
                    } catch (Exception e) {
                        hashData.append(fieldName).append('=').append(fieldValue).append('&');
                    }
                }
            }

            // Remove trailing &
            if (hashData.length() > 0) {
                hashData.setLength(hashData.length() - 1);
            }

            log.info("Hash data string: {}", hashData.toString());

            String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());

            log.info("VNPay verification - Calculated hash: {}", calculatedHash);
            log.info("VNPay verification - Received hash: {}", secureHash);

            return calculatedHash.equals(secureHash);
        } catch (Exception e) {
            log.error("Error verifying VNPay payment", e);
            return false;
        }
    }

    // Temporary bypass cho VNPay sandbox testing
    public boolean verifyPaymentWithBypass(Map<String, String> params) {
        // Cho sandbox VNPay, tạm thời skip verify nếu có responseCode = 00
        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            log.warn("BYPASSING signature verification for sandbox testing");
            return true;
        }

        return verifyPayment(params);
    }

    public void processPayment(Map<String, String> params) {
        try {
            String responseCode = params.get("vnp_ResponseCode");
            String orderInfo = params.get("vnp_OrderInfo");
            String transactionId = params.get("vnp_TxnRef");
            String amount = params.get("vnp_Amount");

            log.info("Processing payment - Response code: {}, Order info: {}, Transaction: {}",
                    responseCode, orderInfo, transactionId);

            // Kiểm tra responseCode phải là "00" (thành công)
            if (!"00".equals(responseCode)) {
                log.warn("Payment failed with response code: {}, skipping membership creation", responseCode);
                return;
            }

            // Kiểm tra transactionId không được null
            if (transactionId == null || transactionId.isEmpty()) {
                log.error("Transaction ID is missing, cannot process payment");
                return;
            }

            PaymentInfo paymentInfo = parseOrderInfo(orderInfo);
            if (paymentInfo == null) {
                log.error("Cannot parse order info: {}", orderInfo);
                return;
            }

            log.info("Parsed payment info - User ID: {}, Package ID: {}",
                    paymentInfo.getUserId(), paymentInfo.getPackageId());

            // Kiểm tra Member và Package tồn tại
            Optional<Member> memberOpt = memberRepository.findByUserId(paymentInfo.getUserId());
            Optional<MembershipPackage> packageOpt = membershipPackageRepository.findById(paymentInfo.getPackageId());

            if (memberOpt.isEmpty()) {
                log.error("Member not found for User ID: {}", paymentInfo.getUserId());
                return;
            }

            if (packageOpt.isEmpty()) {
                log.error("Package not found with ID: {}", paymentInfo.getPackageId());
                return;
            }

            Member member = memberOpt.get();

            log.info("Found Member with userId: {}, creating membership with package: {}",
                    member.getUserId(), paymentInfo.getPackageId());

            // Tạo UserMembership với Member.userId (vì @Id là userId)
            createUserMembership(member.getUserId(), paymentInfo.getPackageId(), packageOpt.get(),transactionId);

            log.info("Successfully created membership for user {} with package {}",
                    member.getUserId(), paymentInfo.getPackageId());

        } catch (Exception e) {
            log.error("Error processing payment", e);
        }
    }

    private PaymentInfo parseOrderInfo(String orderInfo) {
        try {
            String[] parts = orderInfo.split("\\|");
            Long userId = null;
            Long packageId = null;

            for (String part : parts) {
                if (part.startsWith("USER_ID:")) {
                    userId = Long.parseLong(part.substring(8));
                } else if (part.startsWith("PACKAGE_ID:")) {
                    packageId = Long.parseLong(part.substring(11));
                }
            }

            if (userId != null && packageId != null) {
                return new PaymentInfo(userId, packageId);
            }
        } catch (Exception e) {
            log.error("Error parsing order info: {}", orderInfo, e);
        }
        return null;
    }

    private void createUserMembership(Long memberId, Long packageId, MembershipPackage membershipPackage, String transactionId) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);

        UserMembershipRequest request = UserMembershipRequest.builder()
                .userId(memberId)
                .membershipPackageId(packageId)
                .startDate(startDate)
                .endDate(endDate)
                .status("ACTIVE")
                .transactionId(transactionId)
                .build();

        log.info("Creating UserMembership - Member userId: {}, Package ID: {}, Start: {}, End: {} , Transaction ID: {}",
                memberId, packageId, startDate, endDate,transactionId);

        try {
            userMembershipService.create(request);
            log.info("Successfully created UserMembership for member: {}", memberId);
        } catch (Exception e) {
            log.error("Failed to create UserMembership for member: {}", memberId, e);
            throw e;
        }
    }

    private static class PaymentInfo {
        private final Long userId;
        private final Long packageId;

        public PaymentInfo(Long userId, Long packageId) {
            this.userId = userId;
            this.packageId = packageId;
        }

        public Long getUserId() { return userId; }
        public Long getPackageId() { return packageId; }
    }
}
