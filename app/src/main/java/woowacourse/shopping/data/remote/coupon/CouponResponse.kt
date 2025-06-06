package woowacourse.shopping.data.remote.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("discount")
    val discount: Int? = null,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int? = null,
    @SerialName("getQuantity")
    val getQuantity: Int? = null,
    @SerialName("availableTime")
    val availableTime: AvailableTime? = null,
) {
    @Serializable
    data class AvailableTime(
        @SerialName("start")
        val start: String,
        @SerialName("end")
        val end: String,
    )
}
