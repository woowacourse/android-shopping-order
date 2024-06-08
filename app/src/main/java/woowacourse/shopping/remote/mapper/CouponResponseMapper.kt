package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.BuyXGetYDiscountStrategy
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.DiscountStrategy
import woowacourse.shopping.domain.model.DiscountType
import woowacourse.shopping.domain.model.FixedDiscountStrategy
import woowacourse.shopping.domain.model.FreeShippingDiscountStrategy
import woowacourse.shopping.domain.model.PercentageDiscountStrategy
import woowacourse.shopping.remote.dto.response.CouponResponse
import java.time.LocalDate
import java.time.LocalTime

fun CouponResponse.toDomain(
    shippingFee: Int,
    orderedTime: LocalTime,
    orderedItems: List<Cart>,
): Coupon? {
    return Coupon(
        id = id,
        code = code,
        description = description,
        expirationDate = LocalDate.parse(expirationDate),
        discountStrategy = this.generateDiscountStrategy(shippingFee, orderedTime, orderedItems) ?: return null,
        minimumOrderedAmount = minimumAmount ?: 0,
    )
}

fun CouponResponse.generateDiscountStrategy(
    shippingFee: Int,
    orderedTime: LocalTime,
    orderedItems: List<Cart>,
): DiscountStrategy? {
    val discountTypeDomain = DiscountType.convertStringToType(this.discountType)
    return when (discountTypeDomain) {
        DiscountType.FIXED -> {
            FixedDiscountStrategy(orderedItems, discount ?: return null, minimumAmount ?: return null)
        }
        DiscountType.BUY_X_GET_Y -> {
            BuyXGetYDiscountStrategy(orderedItems, buyQuantity ?: return null, getQuantity ?: return null)
        }
        DiscountType.FREE_SHIPPING -> {
            FreeShippingDiscountStrategy(orderedItems, shippingFee, minimumAmount ?: return null)
        }
        DiscountType.PERCENTAGE -> {
            PercentageDiscountStrategy(orderedItems, discount ?: return null, availableTime?.toDomain() ?: return null, orderedTime)
        }
        DiscountType.INVALID -> null
    }
}
