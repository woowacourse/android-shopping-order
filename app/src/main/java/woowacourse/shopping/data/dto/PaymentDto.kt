package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse(
    val originalPayment: Int = 0,
    val finalPayment: Int = 0,
    val usedPoint: Int = 0,
)

@Serializable
data class PaymentRequest(
    val originalPayment: Int,
    val finalPayment: Int,
    val usedPoint: Int,
)
