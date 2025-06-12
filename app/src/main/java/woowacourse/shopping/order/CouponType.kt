package woowacourse.shopping.order

import woowacourse.shopping.product.catalog.ProductUiModel

sealed class CouponType {
    abstract fun isConditionMet(
        coupon: Coupon,
        products: List<ProductUiModel>,
    ): Boolean

    abstract fun discount(
        coupon: Coupon,
        products: List<ProductUiModel>,
    ): Int

    data object Fixed : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = products.sumOf { it.price * it.quantity } >= coupon.minimumAmount

        override fun discount(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Int = coupon.discount
    }

    data object BuyAndGet : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = products.any { it.quantity >= coupon.buyQuantity + coupon.getQuantity }

        override fun discount(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Int {
            val products: List<ProductUiModel> =
                products.filter { it.quantity >= coupon.buyQuantity + coupon.getQuantity }
            val bestPrice = products.maxOf { it.price }
            return bestPrice * coupon.getQuantity
        }
    }

    data object FreeShipping : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = products.sumOf { it.price * it.quantity } >= MINIMUM_TOTAL_PRICE_FOR_FREE_SHIPPING

        override fun discount(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Int = 0
    }

    data object Percentage : CouponType() {
        override fun isConditionMet(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Boolean = true

        override fun discount(
            coupon: Coupon,
            products: List<ProductUiModel>,
        ): Int = (products.sumOf { it.price * it.quantity } * coupon.discount / 100).toInt()
    }

    companion object {
        private const val MINIMUM_TOTAL_PRICE_FOR_FREE_SHIPPING = 50000
    }
}
