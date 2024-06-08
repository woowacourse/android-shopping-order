package woowacourse.shopping.data.dto.response

import woowacourse.shopping.domain.BuyXGetYCoupon
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.DiscountType
import woowacourse.shopping.domain.FixedCoupon
import woowacourse.shopping.domain.FreeShippingCoupon
import woowacourse.shopping.domain.PercentageCoupon
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class CouponDTO(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val discountType: String,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeDto? = null,
) {
    fun toDomain(): Coupon {
        return when (DiscountType.from(discountType)) {
            DiscountType.FreeShipping -> {
                FreeShippingCoupon(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = toLocalDate(expirationDate),
                    discountType = DiscountType.FreeShipping,
                )
            }

            DiscountType.Fixed -> {
                FixedCoupon(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = toLocalDate(expirationDate),
                    discountType = DiscountType.Fixed,
                    discount = discount!!,
                    minimumAmount = minimumAmount!!,
                )
            }

            DiscountType.BuyXGetY -> {
                BuyXGetYCoupon(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = toLocalDate(expirationDate),
                    discountType = DiscountType.BuyXGetY,
                    buyQuantity = buyQuantity!!,
                    getQuantity = getQuantity!!,
                )
            }

            DiscountType.Percentage -> {
                PercentageCoupon(
                    id = id,
                    code = code,
                    description = description,
                    expirationDate = toLocalDate(expirationDate),
                    discountType = DiscountType.Percentage,
                    discount = discount!!,
                    availableTime = availableTime!!.toDomain(),
                )
            }
        }
    }

    private fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }
}

data class AvailableTimeDto(
    val start: String,
    val end: String,
) {
    fun toDomain(): AvailableTime =
        AvailableTime(
            start = toLocalTime(start),
            end = toLocalTime(end),
        )

    private fun toLocalTime(timeString: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return LocalTime.parse(timeString, formatter)
    }
}

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)
