package woowacourse.shopping.domain

import woowacourse.shopping.order.Coupon
import woowacourse.shopping.order.CouponType
import woowacourse.shopping.product.catalog.ProductUiModel

data class OrderingProducts(
    val products: List<ProductUiModel>,
    val defaultShippingFee: Int = 3000,
) {
    private var currentCoupon: Coupon? = null

    fun availableCoupons(coupons: List<Coupon>): List<Coupon> = coupons.filter { it.isConditionMet(products) == true }

    fun discountAmount(): Int {
        currentCoupon?.let { return it.couponType.discount(it, products) }
        return 0
    }

    fun isFreeShipping(): Boolean =
        currentCoupon?.couponType == CouponType.FreeShipping &&
            originalTotalPrice() > (currentCoupon?.minimumAmount ?: 0)

    fun originalTotalPrice(): Int = products.sumOf { it.price * it.quantity }

    fun totalPrice(): Int = originalTotalPrice() - discountAmount() + if (isFreeShipping()) 0 else defaultShippingFee
}
