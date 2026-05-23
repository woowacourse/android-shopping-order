package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.Money
import java.time.LocalDate

@Serializable
data class CouponResponse(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: DiscountType,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeResponse? = null,
)

fun CouponResponse.toDomain(): Coupon = when (discountType) {
    DiscountType.FIXED -> {
        val discountAmount = requireNotNull(discount) {
            "FIXED 쿠폰에 discount가 누락되었습니다 (code=$code)"
        }
        val minAmount = requireNotNull(minimumAmount) {
            "FIXED 쿠폰에 minimumAmount가 누락되었습니다 (code=$code)"
        }

        Coupon.FixedDiscount(
            id = id,
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = Money(discountAmount.toLong()),
            minimumAmount = Money(minAmount.toLong()),
        )
    }

    DiscountType.BUY_X_GET_Y -> {
        val buy = requireNotNull(buyQuantity) {
            "BUY_X_GET_Y 쿠폰에 buyQuantity가 누락되었습니다 (code=$code)"
        }
        val get = requireNotNull(getQuantity) {
            "BUY_X_GET_Y 쿠폰에 getQuantity가 누락되었습니다 (code=$code)"
        }

        Coupon.BuyXGetY(
            id = id,
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            buyQuantity = buy,
            getQuantity = get,
        )
    }

    DiscountType.FREE_SHIPPING -> {
        val minAmount = requireNotNull(minimumAmount) {
            "FREE_SHIPPING 쿠폰에 minimumAmount가 누락되었습니다 (code=$code)"
        }

        Coupon.FreeShipping(
            id = id,
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            minimumAmount = Money(minAmount.toLong()),
        )
    }

    DiscountType.PERCENTAGE -> {
        val percent = requireNotNull(discount) {
            "PERCENTAGE 쿠폰에 discount가 누락되었습니다 (code=$code)"
        }
        val time = requireNotNull(availableTime) {
            "PERCENTAGE 쿠폰에 availableTime이 누락되었습니다 (code=$code)"
        }

        Coupon.PercentageDiscount(
            id = id,
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discountPercent = percent,
            availableTime = time.toDomain(),
        )
    }
}
