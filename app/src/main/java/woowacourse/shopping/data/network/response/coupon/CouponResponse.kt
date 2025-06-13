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
        if (buyQuantity == null) {
            throw IllegalArgumentException("buyQuantity가 필요합니다.")
        }
        if (getQuantity == null) {
            throw IllegalArgumentException("buyQuantity가 필요합니다.")
        }

        return BogoCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
        )
    }

    fun toFixedCoupon(): FixedCoupon {
        if (discount == null) {
            throw IllegalArgumentException("discount가 필요합니다.")
        }
        if (minimumAmount == null) {
            throw IllegalArgumentException("minimumAmount가 필요합니다.")
        }
        return FixedCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount,
            minimumAmount = minimumAmount,
        )
    }

    fun toFreeShippingCoupon(): FreeShippingCoupon {
        if (minimumAmount == null) {
            throw IllegalArgumentException("minimumAmount가 필요합니다.")
        }
        return FreeShippingCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            minimumAmount = minimumAmount,
        )
    }

    fun toMiracleSaleCoupon(): MiracleSaleCoupon {
        if (discount == null) {
            throw IllegalArgumentException("discount가 필요합니다.")
        }
        return MiracleSaleCoupon(
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount,
            timeSlot =
                woowacourse.shopping.domain.coupon.TimeSlot(
                    LocalTime.parse(availableTime?.start),
                    LocalTime.parse(availableTime?.end),
                ),
        )
    }
}
