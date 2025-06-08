package woowacourse.shopping.data.coupon.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponseItem(
    @SerialName("availableTime")
    val availableTime: AvailableTime? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int = 0,
    @SerialName("code")
    val code: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("discount")
    val discount: Int = 0,
    @SerialName("discountType")
    val discountType: String?,
    @SerialName("expirationDate")
    val expirationDate: String?,
    @SerialName("getQuantity")
    val getQuantity: Int = 0,
    @SerialName("id")
    val id: Long?,
    @SerialName("minimumAmount")
    val minimumAmount: Int = 0
) {
    @Serializable
    data class AvailableTime(
        @SerialName("end")
        val end: String?,
        @SerialName("start")
        val start: String?
    )
}