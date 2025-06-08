package woowacourse.shopping.data.model.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    @SerialName("id") val id: Long,
    @SerialName("code") val code: String,
    @SerialName("description") val description: String,
    @SerialName("discountType") val discountType: String,
    @SerialName("discount") val discount: Int,
    @SerialName("minimumAmount") val minimumAmount: Int,
    @SerialName("buyQuantity") val buyQuantity: Int,
    @SerialName("getQuantity") val getQuantity: Int,
    @SerialName("expirationDate") val expirationDate: String,
    @SerialName("availableTime") val availableTime: AvailableTime,
)
