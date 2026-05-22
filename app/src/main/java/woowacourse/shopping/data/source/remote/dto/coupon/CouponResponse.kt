package woowacourse.shopping.data.source.remote.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface CouponResponse {
    val id: Long
    val code: String
    val description: String
    val expirationDate: String

    @Serializable
    @SerialName("FIXED")
    data class Fixed(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Long,
    ) : CouponResponse

    @Serializable
    @SerialName("BUY_X_GET_Y")
    data class BuyXGetY(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : CouponResponse

    @Serializable
    @SerialName("FREE_SHIPPING")
    data class FreeShipping(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumAmount: Long,
    ) : CouponResponse

    @Serializable
    @SerialName("PERCENTAGE")
    data class Percentage(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int,
        val availableTime: AvailableTime,
    ) : CouponResponse
}

@Serializable
data class AvailableTime(
    val start: String,
    val end: String,
)
