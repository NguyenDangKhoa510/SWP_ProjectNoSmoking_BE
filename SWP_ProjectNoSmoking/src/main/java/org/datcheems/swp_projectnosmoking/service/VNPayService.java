package org.datcheems.swp_projectnosmoking.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.config.VNPayConfig;
import org.datcheems.swp_projectnosmoking.utils.VNPayUtil;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;

    public String createPaymentUrl(HttpServletRequest request, long amount, String orderInfo) {
        try {
            String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
            String vnp_IpAddr = request.getRemoteAddr();
            String vnp_TmnCode = vnPayConfig.getTmnCode();

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu nhân 100
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            // Thêm thời gian tạo
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", now.format(formatter));

            // Sắp xếp các key theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String value = URLEncoder.encode(vnp_Params.get(fieldName), StandardCharsets.US_ASCII);
                hashData.append(fieldName).append('=').append(value).append('&');
                query.append(fieldName).append('=').append(value).append('&');
            }

            // Xóa dấu & cuối
            hashData.setLength(hashData.length() - 1);
            query.setLength(query.length() - 1);

            // Tạo secure hash
            String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            return vnPayConfig.getPayUrl() + "?" + query;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
