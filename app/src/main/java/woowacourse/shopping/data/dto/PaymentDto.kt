package woowacourse.shopping.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse(
    @SerialName("originalPayment")
    val originalPayment: Int = 0,
    @SerialName("finalPayment")
    val finalPayment: Int = 0,
    @SerialName("point")
    val point: Int = 0,
)

@Serializable
data class PaymentRequest(
    @SerialName("originalPayment")
    val originalPayment: Int,
    @SerialName("finalPayment")
    val finalPayment: Int,
    @SerialName("point")
    val usedPoint: Int,
)
