package woowacourse.shopping.data.model.coupon

import woowacourse.shopping.domain.model.Coupon
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.LocalTime

data class CouponResponseItem(
    val id: Int,
    val availableTime: AvailableTime?,
    val buyQuantity: Int?,
    val code: String,
    val description: String,
    val discount: Int?,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int?,
    val minimumAmount: Int?
)

fun CouponResponseItem.toCoupon(): Coupon {
    return when {
        discountType == "fixed" && discount != null && minimumAmount != null -> {
            Coupon.Fixed(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                discount = discount,
                minimumAmount = minimumAmount
            )
        }

        discountType == "buyXgetY" && buyQuantity != null && getQuantity != null -> {
            Coupon.BuyXGetY(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity
            )
        }

        discountType == "freeShipping" && minimumAmount != null -> {
            Coupon.FreeShipping(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                minimumAmount = minimumAmount
            )
        }

        discountType == "percentage" && discount != null && availableTime != null -> {
            Coupon.MiracleSale(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                discount = discount,
                startTime = LocalTime.parse(availableTime.start),
                endTime = LocalTime.parse(availableTime.end),
            )
        }

        else -> throw RuntimeException("쿠폰 타입 에러")
    }
}
