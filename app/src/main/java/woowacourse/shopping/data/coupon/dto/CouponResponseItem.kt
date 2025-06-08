package woowacourse.shopping.data.coupon.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponseItem(
    @SerialName("availableTime")
    val availableTime: AvailableTime?,
    @SerialName("buyQuantity")
    val buyQuantity: Int?,
    @SerialName("code")
    val code: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("discount")
    val discount: Int?,
    @SerialName("discountType")
    val discountType: String?,
    @SerialName("expirationDate")
    val expirationDate: String?,
    @SerialName("getQuantity")
    val getQuantity: Int?,
    @SerialName("id")
    val id: Long?,
    @SerialName("minimumAmount")
    val minimumAmount: Int?
){
    @Serializable
    data class AvailableTime(
        @SerialName("end")
        val end: String?,
        @SerialName("start")
        val start: String?
    )
}