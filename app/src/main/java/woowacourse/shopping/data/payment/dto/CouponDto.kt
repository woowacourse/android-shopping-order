package woowacourse.shopping.data.payment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("discountType")
sealed class CouponDto {
    abstract val id: Int
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String

    @Serializable
    @SerialName("fixed")
    data class FixedDto(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int? = null,
        val minimumAmount: Int? = null,
    ) : CouponDto()

    @Serializable
    @SerialName("buyXgetY")
    data class BonusGoodsDto(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int? = null,
        val getQuantity: Int? = null,
    ) : CouponDto()

    @Serializable
    @SerialName("freeShipping")
    data class FreeShippingDto(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumAmount: Int? = null,
    ) : CouponDto()

    @Serializable
    @SerialName("percentage")
    data class PercentageDto(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int? = null,
        val availableTime: AvailableTime? = null,
    ) : CouponDto()
}
