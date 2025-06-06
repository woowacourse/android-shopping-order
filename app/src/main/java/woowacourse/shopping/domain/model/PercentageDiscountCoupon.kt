package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Price.Companion.DEFAULT_SHIPPING_PRICE
import woowacourse.shopping.domain.model.Price.Companion.MINIMUM_PRICE
import java.time.LocalDateTime

class PercentageDiscountCoupon(
    override val detail: CouponDetail,
    override val isSelected: Boolean = false,
) : Coupon {
    override fun apply(products: Products): Price {
        val original = products.selectedProductsPrice

        val isAvailable = getIsAvailable(products)

        val discountRate = (detail.discount ?: MINIMUM_PRICE).coerceIn(0, 100)
        val discount = if (isAvailable) (original * discountRate / 100.0).toInt() else MINIMUM_PRICE

        val shipping = DEFAULT_SHIPPING_PRICE

        return Price(
            original = original,
            discount = discount,
            shipping = shipping,
        )
    }

    override fun getIsAvailable(
        products: Products,
        nowDateTime: LocalDateTime,
    ): Boolean {
        val minimumPurchase = detail.minimumPurchase ?: MINIMUM_PRICE
        val now = nowDateTime.toLocalTime()

        val isAmountOkay = products.selectedProductsPrice >= minimumPurchase
        val isTimeOkay =
            detail.availableTime?.let {
                now.isAfter(it.start) && now.isBefore(it.end)
            } ?: true

        val isDateOkay = detail.expirationDate >= nowDateTime.toLocalDate()

        return isAmountOkay && isTimeOkay && isDateOkay
    }

    override fun copy(
        detail: CouponDetail,
        isSelected: Boolean,
    ): Coupon = PercentageDiscountCoupon(detail, isSelected)
}
