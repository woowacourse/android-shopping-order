package woowacourse.shopping.data.network.response.coupon

import woowacourse.shopping.domain.coupon.BogoCoupon
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDate
import java.time.LocalTime

data class CouponResponseItem(
    val availableTime: AvailableTime,
    val buyQuantity: Int,
    val code: String,
    val description: String,
    val discount: Int,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int,
    val id: Int,
    val minimumAmount: Int
) {
    fun toBogoCoupon(): BogoCoupon {
        return BogoCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
        )
    }

    fun toFixedCoupon(): FixedCoupon {
        return FixedCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount,
            minimumAmount = minimumAmount
        )
    }

    fun toFreeShippingCoupon(): FreeShippingCoupon {
        return FreeShippingCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            minimumAmount = minimumAmount
        )
    }

    fun toMiracleSaleCoupon(): MiracleSaleCoupon {
        return MiracleSaleCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount,
            availableTime = woowacourse.shopping.domain.coupon.AvailableTime(LocalTime.parse(availableTime.start), LocalTime.parse(availableTime.end))
        )
    }
}
