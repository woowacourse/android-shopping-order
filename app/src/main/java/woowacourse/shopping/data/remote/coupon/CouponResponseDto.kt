package woowacourse.shopping.data.remote.coupon

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponPolicy
import woowacourse.shopping.domain.model.coupon.FreeShippingPolicy
import woowacourse.shopping.domain.model.coupon.OrderFixedAmountDiscountPolicy
import woowacourse.shopping.domain.model.coupon.OrderPercentageDiscountPolicy
import woowacourse.shopping.domain.model.coupon.SameProductQuantityDiscountPolicy
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CouponResponseDto(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val minimumAmount: Int? = null,
    val discount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeResponseDto? = null,
    val discountType: String,
) {
    fun toDomain(): Coupon {
        val normalizedDiscountType = discountType.lowercase()
        val availableHourRange = availableTime?.toHourRange()

        return Coupon(
            id = id,
            code = code,
            title = description,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            minimumOrderAmount = minimumAmount,
            availableFromHour = availableHourRange?.first,
            availableToHourExclusive = availableHourRange?.second,
            policy = toDiscountPolicy(normalizedDiscountType),
        )
    }

    private fun toDiscountPolicy(normalizedDiscountType: String): CouponPolicy =
        when (normalizedDiscountType) {
            FIXED_DISCOUNT_TYPE -> OrderFixedAmountDiscountPolicy(amount = requireDiscount())
            PERCENTAGE_DISCOUNT_TYPE -> OrderPercentageDiscountPolicy(rate = requireDiscount())
            BUY_X_GET_Y_DISCOUNT_TYPE ->
                SameProductQuantityDiscountPolicy(
                    requiredSameProductQuantity = requireBuyQuantity() + requireGetQuantity(),
                )
            FREE_SHIPPING_DISCOUNT_TYPE -> FreeShippingPolicy
            else -> throw IllegalArgumentException("지원하지 않는 쿠폰 타입입니다: $discountType")
        }

    private fun requireDiscount(): Int =
        requireNotNull(discount) {
            "discountType=$discountType 쿠폰에는 discount 값이 필요합니다."
        }

    private fun requireBuyQuantity(): Int =
        requireNotNull(buyQuantity) {
            "discountType=$discountType 쿠폰에는 buyQuantity 값이 필요합니다."
        }

    private fun requireGetQuantity(): Int =
        requireNotNull(getQuantity) {
            "discountType=$discountType 쿠폰에는 getQuantity 값이 필요합니다."
        }

    companion object {
        private const val FIXED_DISCOUNT_TYPE = "fixed"
        private const val PERCENTAGE_DISCOUNT_TYPE = "percentage"
        private const val BUY_X_GET_Y_DISCOUNT_TYPE = "buyxgety"
        private const val FREE_SHIPPING_DISCOUNT_TYPE = "freeshipping"
    }
}

@Serializable
data class AvailableTimeResponseDto(
    val start: String,
    val end: String,
) {
    fun toHourRange(): Pair<Int, Int> {
        val startTime = LocalTime.parse(start)
        val endTime = LocalTime.parse(end)
        val endHourExclusive =
            if (endTime.minute == 0 && endTime.second == 0 && endTime.nano == 0) {
                endTime.hour
            } else {
                (endTime.hour + 1).coerceAtMost(24)
            }

        return startTime.hour to endHourExclusive
    }
}
