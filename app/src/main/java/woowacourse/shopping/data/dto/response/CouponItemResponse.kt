package woowacourse.shopping.data.dto.response

import woowacourse.shopping.domain.BuyXGetYCoupon
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.DiscountType
import woowacourse.shopping.domain.FixedCoupon
import woowacourse.shopping.domain.FreeShippingCoupon
import woowacourse.shopping.domain.PercentageCoupon
import java.time.LocalDate

data class CouponItemResponse(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val discountType: String,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeResponse? = null,
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
                    isChecked = false,
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
                    isChecked = false,
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
                    isChecked = false,
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
                    isChecked = false,
                )
            }
        }
    }

    private fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }
}
