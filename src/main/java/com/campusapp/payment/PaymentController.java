package com.campusapp.payment;

import com.campusapp.common.ApiResponse;
import com.campusapp.dto.PaymentOrderResponse;
import com.campusapp.dto.SlotStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate/{registrationId}")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> initiatePayment(
            @PathVariable Long registrationId,
            Principal principal) {
        PaymentOrderResponse response = paymentService
                .initiatePayment(principal.getName(), registrationId);
        return ResponseEntity.ok(
                ApiResponse.success("Payment order created", response));
    }

    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> confirmPayment(
            @PathVariable String orderId,
            Principal principal) {
        PaymentOrderResponse response = paymentService
                .confirmPayment(principal.getName(), orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Payment confirmed successfully", response));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> cancelPayment(
            @PathVariable String orderId,
            Principal principal) {
        PaymentOrderResponse response = paymentService
                .cancelPayment(principal.getName(), orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Payment cancelled", response));
    }
 // NEW — Get payment order status
    @GetMapping("/order/{orderId}/status")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> getOrderStatus(
            @PathVariable String orderId,
            Principal principal) {
        PaymentOrderResponse response = paymentService
                .getOrderStatus(principal.getName(), orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Order status fetched", response));
    }

    @GetMapping("/slot-status/{eventId}")
    public ResponseEntity<ApiResponse<SlotStatusResponse>> getSlotStatus(
            @PathVariable Long eventId) {
        SlotStatusResponse response = paymentService.getSlotStatus(eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Slot status fetched", response));
    }
}