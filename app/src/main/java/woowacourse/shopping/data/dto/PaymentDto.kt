package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse(
    @SerializedName("originalPayment")
    val originalPayment: Int = 0,
    @SerializedName("finalPayment")
    val finalPayment: Int = 0,
    @SerializedName("usedPoint")
    val usedPoint: Int = 0,
)

@Serializable
data class PaymentRequest(
    @SerializedName("originalPayment")
    val originalPayment: Int,
    @SerializedName("finalPayment")
    val finalPayment: Int,
    @SerializedName("usedPoint")
    val usedPoint: Int,
)
