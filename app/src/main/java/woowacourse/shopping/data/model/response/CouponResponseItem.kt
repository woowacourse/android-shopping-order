package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponseItem(
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
    val discount: Int?,
    @SerialName("minimumAmount")
    val minimumAmount: Int?,
    @SerialName("buyQuantity")
    val buyQuantity: Int?,
    @SerialName("getQuantity")
    val getQuantity: Int?,
    @SerialName("availableTime")
    val availableTime: AvailableTime?,
) {
    @Serializable
    data class AvailableTime(
        @SerialName("start")
        val start: String,
        @SerialName("end")
        val end: String,
    )
}
