package woowacourse.shopping.data.coupon.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponseItem(
    @SerialName("id")
    val id: Int,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("discount")
    val discount: Int? = null,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
    @SerialName("availableTime")
    val availableTime: AvailableTime? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int? = null,
    @SerialName("getQuantity")
    val getQuantity: Int? = null,
)
