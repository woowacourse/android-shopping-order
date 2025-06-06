package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Price.Companion.DEFAULT_SHIPPING_PRICE
import woowacourse.shopping.domain.model.Price.Companion.MINIMUM_PRICE
import java.time.LocalDate

class FreeShippingCoupon(
    override val detail: CouponDetail,
    override val isSelected: Boolean = false,
) : Coupon {
    override fun apply(products: Products): Price {
        val original = products.selectedProductsPrice

        val isAvailable = getIsAvailable(products)

        val discount = if (isAvailable) DEFAULT_SHIPPING_PRICE else MINIMUM_PRICE

        val shipping = DEFAULT_SHIPPING_PRICE

        return Price(
            original = original,
            discount = discount,
            shipping = shipping,
        )
    }

    override fun getIsAvailable(
        products: Products,
        nowDate: LocalDate,
    ): Boolean {
        val isExceedingMinimumPurchase = products.selectedProductsPrice >= (detail.minimumPurchase ?: MINIMUM_PRICE)
        val isDateOkay = detail.expirationDate >= nowDate

        return isExceedingMinimumPurchase && isDateOkay
    }

    override fun copy(
        detail: CouponDetail,
        isSelected: Boolean,
    ): Coupon = FreeShippingCoupon(detail, isSelected)
}
