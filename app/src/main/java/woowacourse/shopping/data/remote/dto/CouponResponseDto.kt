package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface CouponResponseDto {
    val id: Int
    val code: String
    val description: String
    val expirationDate: String
    val discountType: String

    @Serializable
    @SerialName("fixed")
    data class Fixed(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        override val discountType: String,
        val discount: Int,
        val minimumAmount: Int,
    ) : CouponResponseDto

    @Serializable
    @SerialName("percentage")
    data class Percentage(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        override val discountType: String,
        val discount: Int,
        val availableTime: TimeRangeDto,
    ) : CouponResponseDto

    @Serializable
    @SerialName("buyXgetY")
    data class BuyXGetY(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        override val discountType: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : CouponResponseDto

    @Serializable
    @SerialName("freeShipping")
    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        override val discountType: String,
        val minimumAmount: Int,
    ) : CouponResponseDto
}

@Serializable
data class TimeRangeDto(
    val start: String,
    val end: String,
)
