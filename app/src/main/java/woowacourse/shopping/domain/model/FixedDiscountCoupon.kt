package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Price.Companion.DEFAULT_SHIPPING_PRICE
import woowacourse.shopping.domain.model.Price.Companion.MINIMUM_PRICE
import java.time.LocalDateTime

class FixedDiscountCoupon(
    override val detail: CouponDetail,
    override val isSelected: Boolean = false,
) : Coupon {
    override fun apply(products: Products): Price {
        val original = products.selectedProductsPrice

        val discount =
            if (getIsAvailable(products)) {
                detail.discount ?: MINIMUM_PRICE
            } else {
                MINIMUM_PRICE
            }

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
        val isExceedingMinimumPurchase = products.selectedProductsPrice >= (detail.minimumPurchase ?: MINIMUM_PRICE)
        val isDateOkay = detail.expirationDate >= nowDateTime.toLocalDate()

        return isExceedingMinimumPurchase && isDateOkay
    }

    override fun copy(
        detail: CouponDetail,
        isSelected: Boolean,
    ): Coupon = FixedDiscountCoupon(detail, isSelected)
}
