package woowacourse.shopping.data.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponse(
    @SerialName("availableTime")
    val availableTime: AvailableTime? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int? = null,
    @SerialName("code")
    val code: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("discount")
    val discount: Int? = null,
    @SerialName("discountType")
    val discountType: String?,
    @SerialName("expirationDate")
    val expirationDate: String?,
    @SerialName("getQuantity")
    val getQuantity: Int? = null,
    @SerialName("id")
    val id: Long?,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
) {
    @Serializable
    data class AvailableTime(
        @SerialName("end")
        val end: String?,
        @SerialName("start")
        val start: String?,
    )
}
