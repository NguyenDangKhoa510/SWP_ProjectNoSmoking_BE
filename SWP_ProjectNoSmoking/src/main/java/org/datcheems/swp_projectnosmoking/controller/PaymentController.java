package org.datcheems.swp_projectnosmoking.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.service.PaymentService;
import org.datcheems.swp_projectnosmoking.service.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class PaymentController {

    private final VNPayService vnPayService;
    private final PaymentService paymentService; // Inject PaymentService mới

    /**
     * API tạo URL thanh toán VNPay
     */
    @GetMapping("/create-vnpay")
    public ResponseEntity<String> createPayment(
            HttpServletRequest request,
            @RequestParam long amount,
            @RequestParam String orderInfo) {

        log.info("Creating VNPay payment - Amount: {}, Order info: {}", amount, orderInfo);

        String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderInfo);
        if (paymentUrl == null) {
            return ResponseEntity.badRequest().body("Không thể tạo URL thanh toán.");
        }
        return ResponseEntity.ok(paymentUrl);
    }

    /**
     * API callback sau khi thanh toán (người dùng được chuyển về đây)
     */
    @GetMapping("/vnpay-return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("VNPay return callback received");
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Query String: {}", request.getQueryString());

        // Lấy tất cả parameters từ VNPay
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (value.length > 0) {
                params.put(key, value[0]);
            }
        });

        log.info("VNPay return parameters: {}", params);

        boolean paymentSuccess = false;
        String errorMessage = "";

        // Verify và xử lý thanh toán
        try {
            // Kiểm tra responseCode trước khi verify
            String responseCode = params.get("vnp_ResponseCode");
            if (!"00".equals(responseCode)) {
                log.warn("Payment failed with response code: {}", responseCode);
                errorMessage = "Payment failed with response code: " + responseCode;
                paymentSuccess = false;
            }
            // Chỉ verify và xử lý khi responseCode là 00
            else if (paymentService.verifyPaymentWithBypass(params)) {

                log.info("VNPay signature verified successfully (or bypassed for testing)");
                paymentService.processPayment(params); // Sẽ gọi UserMembershipService.create()
                paymentSuccess = true;
            } else {
                log.error("VNPay signature verification failed");
                errorMessage = "Signature verification failed";
            }
        } catch (Exception e) {
            log.error("Error processing payment", e);
            errorMessage = e.getMessage();
            paymentSuccess = false;
        }

        // Thêm thông tin trạng thái vào params để frontend xử lý
        params.put("payment_processed", paymentSuccess ? "true" : "false");
        if (!paymentSuccess && !errorMessage.isEmpty()) {
            params.put("error_message", errorMessage);
        }

        // Redirect về Frontend với tất cả parameters
        StringBuilder queryString = new StringBuilder();
        params.forEach((key, value) -> {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            try {
                queryString.append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                queryString.append(key).append("=").append(value);
            }
        });

        String frontendUrl = "http://localhost:8080/payment-result?" + queryString.toString();
        log.info("Redirecting to frontend: {}", frontendUrl);
        response.sendRedirect(frontendUrl);
    }

    /**
     * API server nhận thông báo IPN từ VNPay (server-to-server)
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
        log.info("VNPay IPN callback received");

        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (value.length > 0) {
                params.put(key, value[0]);
            }
        });

        log.info("VNPay IPN parameters: {}", params);
        // Kiểm tra responseCode trước khi verify
        String responseCode = params.get("vnp_ResponseCode");
        if (!"00".equals(responseCode)) {
            log.warn("IPN: Payment failed with response code: {}", responseCode);
            return ResponseEntity.ok("99"); // Lỗi
        }


        if (paymentService.verifyPayment(params)) {
            paymentService.processPayment(params); // Cũng sẽ gọi UserMembershipService.create()
            return ResponseEntity.ok("00"); // VNPay yêu cầu trả về "00" khi thành công
        }

        return ResponseEntity.ok("99"); // Lỗi
    }
    @GetMapping("/test-callback")
    public ResponseEntity<String> testCallback() {
        log.info("=== Testing callback manually ===");

        // Giả lập parameters từ VNPay thành công
        Map<String, String> testParams = new HashMap<>();
        testParams.put("vnp_ResponseCode", "00");
        testParams.put("vnp_OrderInfo", "USER_ID:2|PACKAGE_ID:1|PACKAGE_NAME:VIP");
        testParams.put("vnp_TxnRef", "TEST" + System.currentTimeMillis());
        testParams.put("vnp_Amount", "230000000"); // VNPay amount * 100
        testParams.put("vnp_SecureHash", "dummy_hash");

        log.info("Test params: {}", testParams);

        try {
            // Bỏ qua verify signature cho test
            paymentService.processPayment(testParams);
            return ResponseEntity.ok("Test callback processed successfully");
        } catch (Exception e) {
            log.error("Test callback failed", e);
            return ResponseEntity.ok("Test callback failed: " + e.getMessage());
        }
    }

    /**
     * API để Frontend kiểm tra trạng thái callback từ Backend
     */
    @GetMapping("/check-callback")
    public ResponseEntity<Map<String, Object>> checkCallback(
            @RequestParam(required = false) String vnp_TxnRef,
            @RequestParam(required = false) String vnp_ResponseCode) {

        Map<String, Object> result = new HashMap<>();

        log.info("Frontend checking callback status - TxnRef: {}, ResponseCode: {}", vnp_TxnRef, vnp_ResponseCode);

        if (vnp_TxnRef == null || vnp_ResponseCode == null) {
            result.put("status", "error");
            result.put("message", "Missing required parameters");
            return ResponseEntity.badRequest().body(result);
        }

        result.put("status", "success");
        result.put("vnp_TxnRef", vnp_TxnRef);
        result.put("vnp_ResponseCode", vnp_ResponseCode);
        result.put("paymentSuccess", "00".equals(vnp_ResponseCode));
        result.put("message", "00".equals(vnp_ResponseCode) ? "Payment successful" : "Payment failed");

        return ResponseEntity.ok(result);
    }

    @GetMapping("/test-payment-process")
    public ResponseEntity<String> testPaymentProcess() {
        log.info("=== Testing payment processing with real VNPay params ===");

        // Giả lập parameters từ VNPay sandbox thực tế
        Map<String, String> testParams = new HashMap<>();
        testParams.put("vnp_ResponseCode", "00");
        testParams.put("vnp_OrderInfo", "USER_ID:2|PACKAGE_ID:1|PACKAGE_NAME:VIP");
        testParams.put("vnp_TxnRef", "TEST" + System.currentTimeMillis());
        testParams.put("vnp_Amount", "230000000");
        testParams.put("vnp_TransactionStatus", "00");
        testParams.put("vnp_PayDate", "20250706160709");

        log.info("Test params: {}", testParams);

        try {
            // Test chỉ processPayment, skip verify
            paymentService.processPayment(testParams);
            return ResponseEntity.ok("Test payment processing successful");
        } catch (Exception e) {
            log.error("Test payment processing failed", e);
            return ResponseEntity.ok("Test payment processing failed: " + e.getMessage());
        }
    }
}