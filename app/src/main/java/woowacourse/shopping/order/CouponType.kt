package woowacourse.shopping.order

import woowacourse.shopping.product.catalog.ProductUiModel

sealed class CouponType {
    abstract fun isConditionMet(
        coupon: Coupon,
        products: List<ProductUiModel>,
    ): Boolean

    data object Fixed : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = products.sumOf { it.price * it.quantity } >= coupon.minimumAmount
    }

    data object BuyAndGet : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = products.any { it.quantity >= coupon.buyQuantity + coupon.getQuantity }
    }

    data object FreeShipping : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = products.sumOf { it.price * it.quantity } >= MINIMUM_TOTAL_PRICE_FOR_FREE_SHIPPING
    }

    data object Percentage : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = true
    }

    companion object {
        private const val MINIMUM_TOTAL_PRICE_FOR_FREE_SHIPPING = 50000
    }
}
