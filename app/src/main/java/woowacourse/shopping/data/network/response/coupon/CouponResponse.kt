package woowacourse.shopping.data.network.response.coupon

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.coupon.BogoCoupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CouponResponse(
    val availableTime: AvailableTime? = null,
    val buyQuantity: Int? = null,
    val code: String,
    val description: String,
    val discount: Int? = null,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int? = null,
    val id: Int,
    val minimumAmount: Int? = null,
) {
    fun toBogoCoupon(): BogoCoupon {
        return BogoCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            buyQuantity = buyQuantity ?: 2,
            getQuantity = getQuantity ?: 1,
        )
    }

    fun toFixedCoupon(): FixedCoupon {
        return FixedCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount ?: 0,
            minimumAmount = minimumAmount ?: 0,
        )
    }

    fun toFreeShippingCoupon(): FreeShippingCoupon {
        return FreeShippingCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            minimumAmount = minimumAmount ?: 0,
        )
    }

    fun toMiracleSaleCoupon(): MiracleSaleCoupon {
        return MiracleSaleCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount ?: 0,
            timeSlot =
                woowacourse.shopping.domain.coupon.TimeSlot(
                    LocalTime.parse(availableTime?.start),
                    LocalTime.parse(availableTime?.end),
                ),
        )
    }
}
