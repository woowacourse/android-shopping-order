package woowacourse.shopping.data.network.coupon.dto

import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.coupon.BogoCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleMorningCoupon

@Serializable
data class CouponDto(
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
    val discount: Int? = null,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
) {
    fun toDomain(): Coupon? {
        val validUntil = LocalDate.parse(expirationDate)
        return when (discountType) {
            FIXED_DISCOUNT -> FixedDiscountCoupon(
                validUntil = validUntil,
                minimumPrice = minimumAmount ?: 100_000,
                discountPrice = discount ?: 5_000,
            )
            BOGO -> BogoCoupon(validUntil)
            FREE_SHIPPING -> FreeShippingCoupon(
                validUntil = validUntil,
                minimumPrice = minimumAmount ?: 50_000,
            )
            MIRACLE_MORNING -> MiracleMorningCoupon(validUntil)
            else -> null
        }
    }

    companion object {
        private const val FIXED_DISCOUNT = "FIXED_DISCOUNT"
        private const val BOGO = "BOGO"
        private const val FREE_SHIPPING = "FREE_SHIPPING"
        private const val MIRACLE_MORNING = "MIRACLE_MORNING"
    }
}
