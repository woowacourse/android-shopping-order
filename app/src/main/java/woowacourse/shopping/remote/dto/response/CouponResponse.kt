package woowacourse.shopping.remote.dto.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    @SerialName("availableTime")
    val availableTime: AvailableTime = AvailableTime(),
    @SerialName("buyQuantity")
    val buyQuantity: Int = 0,
    @SerialName("code")
    val code: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("discount")
    val discount: Long = 0,
    @SerialName("discountType")
    val discountType: String = "",
    @SerialName("expirationDate")
    val expirationDate: String = "",
    @SerialName("getQuantity")
    val getQuantity: Int = 0,
    @SerialName("id")
    val id: Long = 0,
    @SerialName("minimumAmount")
    val discountableMinPrice: Long = 0
) {
    @Serializable
    data class AvailableTime(
        @SerialName("end")
        val endTime: String = "",
        @SerialName("start")
        val startTime: String = ""
    )
}