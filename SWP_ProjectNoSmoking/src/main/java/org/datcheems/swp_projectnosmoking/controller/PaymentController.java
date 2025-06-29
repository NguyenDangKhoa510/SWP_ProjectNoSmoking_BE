package org.datcheems.swp_projectnosmoking.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.service.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;

    /**
     * API tạo URL thanh toán VNPay
     * Ví dụ gọi: GET /api/payment/create-vnpay?amount=100000&orderInfo=Mua gói thành viên
     */
    @GetMapping("/create-vnpay")
    public ResponseEntity<String> createPayment(
            HttpServletRequest request,
            @RequestParam long amount,
            @RequestParam String orderInfo) {

        String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderInfo);
        if (paymentUrl == null) {
            return ResponseEntity.badRequest().body("Không thể tạo URL thanh toán.");
        }
        return ResponseEntity.ok(paymentUrl);
    }

    /**
     * API callback sau khi thanh toán thành công hoặc thất bại (người dùng được chuyển về đây)
     * Cấu hình trong vnp.returnUrl
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnpayReturn(HttpServletRequest request) {
        // TODO: Kiểm tra responseCode, mã giao dịch, secure hash,...
        return ResponseEntity.ok("Giao dịch đã được xử lý. Vui lòng kiểm tra kết quả thanh toán.");
    }

    /**
     * API server nhận thông báo IPN từ VNPay (server gọi server)
     * Cấu hình trong vnp.ipnUrl
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
        // TODO: Xác thực và xử lý trạng thái thanh toán + tạo UserMembership
        return ResponseEntity.ok("IPN nhận thành công từ VNPay.");
    }
}
