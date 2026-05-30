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
    @SerialName("fixed")
    data class Fixed(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Long,
        val minimumAmount: Long,
    ) : CouponResponse

    @Serializable
    @SerialName("buyXgetY")
    data class BuyXGetY(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : CouponResponse

    @Serializable
    @SerialName("freeShipping")
    data class FreeShipping(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumAmount: Long,
    ) : CouponResponse

    @Serializable
    @SerialName("percentage")
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
