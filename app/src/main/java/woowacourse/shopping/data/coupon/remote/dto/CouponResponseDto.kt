package woowacourse.shopping.data.coupon.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("availableTime")
    val availableTime: AvailableTimeResponseDto? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int = 0,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("discount")
    val discount: Int? = null,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("getQuantity")
    val getQuantity: Int = 0,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
)
