package com.example.data.payment

/**
 * ============================================================
 * FUTURE PAYMENT ARCHITECTURE INTERFACES (SCOPE-LOCKED / FUTURE)
 * ============================================================
 *
 * NOTE: The mobile application does NOT process or settle real payments directly.
 * Payment settlement is strictly backend-authoritative via the DoP Django API.
 *
 * Architecture:
 * Mobile Client
 *      ↓
 * DoP Django API (Server-side)
 *      ↓
 * Payment Gateway Service
 *      ↓
 * Provider Adapters (e.g., AzamPay / Selcom / Pesapal)
 *      ↓
 * Mobile Network Operators / Banks (M-Pesa, Tigo Pesa, Airtel Money, NMB, CRDB)
 *
 * Status: FUTURE INTEGRATION (No live credentials or mock settlements in mobile UI).
 */

enum class SupportedPaymentProvider {
    AZAMPAY,
    SELCOM,
    PESAPAL
}

enum class PaymentInitiationStatus {
    PENDING_BACKEND_DISPATCH,
    AWAITING_CUSTOMER_ACTION,
    FAILED
}

data class PaymentCheckoutRequest(
    val referenceId: String,
    val amountTzs: Long,
    val currency: String = "TZS",
    val customerPhone: String,
    val description: String,
    val callbackUrl: String? = null
)

data class PaymentCheckoutResponse(
    val transactionId: String,
    val status: PaymentInitiationStatus,
    val providerReference: String?,
    val instructions: String
)

interface PaymentGatewayService {
    /**
     * Future integration point to request payment checkout initiation from the DoP Django backend.
     */
    suspend fun initiateCheckout(request: PaymentCheckoutRequest): Result<PaymentCheckoutResponse>

    /**
     * Future integration point to query transaction settlement status from backend.
     */
    suspend fun queryPaymentStatus(transactionId: String): Result<String>
}

/**
 * Stub implementation for development and architectural reference.
 */
class FuturePaymentGatewayStub : PaymentGatewayService {
    override suspend fun initiateCheckout(request: PaymentCheckoutRequest): Result<PaymentCheckoutResponse> {
        return Result.success(
            PaymentCheckoutResponse(
                transactionId = "TX-STUB-${request.referenceId}",
                status = PaymentInitiationStatus.PENDING_BACKEND_DISPATCH,
                providerReference = null,
                instructions = "Payment provider integration is scheduled for future backend release."
            )
        )
    }

    override suspend fun queryPaymentStatus(transactionId: String): Result<String> {
        return Result.success("AWAITING_BACKEND_INTEGRATION")
    }
}
