package woowacourse.shopping.data.coupon.dto


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
sealed class CouponResponseItem {
    abstract val id: Long?
    abstract val code: String?
    abstract val description: String?
    abstract val expirationDate: String?

    @Serializable
    @SerialName("fixed")
    data class FixedDiscountCoupon(
        override val id: Long?,
        override val code: String?,
        override val description: String?,
        override val expirationDate: String?,
        val discount: Int?,
        val minimumAmount: Int?
    ) : CouponResponseItem()

    @Serializable
    @SerialName("buyXgetY")
    data class BuyXGetYCoupon(
        override val id: Long?,
        override val code: String?,
        override val description: String?,
        override val expirationDate: String?,
        val buyQuantity: Int?,
        val getQuantity: Int?
    ) : CouponResponseItem()

    @Serializable
    @SerialName("freeShipping")
    data class FreeShippingCoupon(
        override val id: Long?,
        override val code: String?,
        override val description: String?,
        override val expirationDate: String?,
        val minimumAmount: Int?
    ) : CouponResponseItem()

    @Serializable
    @SerialName("percentage")
    data class PercentageCoupon(
        override val id: Long?,
        override val code: String?,
        override val description: String?,
        override val expirationDate: String?,
        val discount: Int?,
        val availableTime: AvailableTime? = null
    ) : CouponResponseItem() {
        @Serializable
        data class AvailableTime(
            val start: String?,
            val end: String?
        )
    }
}
