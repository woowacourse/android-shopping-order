package woowacourse.shopping.domain

import woowacourse.shopping.order.Coupon
import woowacourse.shopping.product.catalog.ProductUiModel

class OrderingProducts(
    private val products: List<ProductUiModel>,
) {
    fun availableCoupons(coupons: List<Coupon>): List<Coupon> = coupons.filter { it.isConditionMet(products) == true }
}
