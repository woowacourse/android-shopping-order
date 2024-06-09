package woowacourse.shopping.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("code")
    val code: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("expirationDate")
    val expirationDate: String = "",
    @SerialName("discountType")
    val discountType: String = "",
    @SerialName("discount")
    val discount: Int = 0,
    @SerialName("minimumAmount")
    val minimumAmount: Int = 0,
    @SerialName("buyQuantity")
    val buyQuantity: Int = 0,
    @SerialName("getQuantity")
    val getQuantity: Int = 0,
    @SerialName("availableTime")
    val availableTime: AvailableTime = AvailableTime(),
)

@Serializable
data class AvailableTime(
    @SerialName("start")
    val start: String = "",
    @SerialName("end")
    val end: String = "",
)
