package woowacourse.shopping.data.dto

data class PaymentResponse(
    val originalPayment: Int,
    val finalPayment: Int,
    val usedPoint: Int,
)

data class PaymentRequest(
    val originalPayment: Int,
    val finalPayment: Int,
    val usedPoint: Int,
)
