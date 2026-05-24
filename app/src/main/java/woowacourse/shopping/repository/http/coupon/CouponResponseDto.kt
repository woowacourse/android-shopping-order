package woowacourse.shopping.repository.http.coupon

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Coupon
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
            fixedDiscountAmount = discount.takeIf { normalizedDiscountType == FIXED_DISCOUNT_TYPE },
            percentageDiscountRate = discount.takeIf { normalizedDiscountType == PERCENTAGE_DISCOUNT_TYPE },
            requiredSameProductQuantity =
                if (normalizedDiscountType == BUY_X_GET_Y_DISCOUNT_TYPE) {
                    buyQuantity?.plus(getQuantity ?: 0)
                } else {
                    null
                },
            freeShipping = normalizedDiscountType == FREE_SHIPPING_DISCOUNT_TYPE,
            bogoEligible = normalizedDiscountType == BUY_X_GET_Y_DISCOUNT_TYPE,
            availableFromHour = availableHourRange?.first,
            availableToHourExclusive = availableHourRange?.second,
        )
    }

    private fun AvailableTimeResponseDto.toHourRange(): Pair<Int, Int> {
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
)
