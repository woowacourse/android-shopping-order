package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.DiscountType

class Order(val coupons: List<Coupon>) {
    fun canUseCoupons(products: List<CartWithProduct>): List<Coupon> = coupons.filter { it.canUse(products) }

    fun discountPrice(
        products: List<CartWithProduct>,
        selectedCouponId: Long,
    ): Int {
        require(canUseCoupons(products).map { it.id }.contains(selectedCouponId)) {
            "$selectedCouponId 에 해당하는 쿠폰을 사용할 수 없습니다."
        }
        val coupon = coupons.first { it.id == selectedCouponId }
        return coupon.discountPrice(products)
    }

    fun shippingPrice(
        products: List<CartWithProduct>,
        selectedCouponId: Long,
    ): Int {
        require(canUseCoupons(products).map { it.id }.contains(selectedCouponId)) {
            "$selectedCouponId 에 해당하는 쿠폰을 사용할 수 없습니다."
        }

        val coupon = coupons.first { it.id == selectedCouponId }
        return if (coupon.discountType == DiscountType.FreeShipping) SHIPPING_FREE_PRICE else SHIPPING_PRICE
    }

    fun paymentPrice(
        products: List<CartWithProduct>,
        selectedCouponId: Long,
    ): Int {
        require(canUseCoupons(products).map { it.id }.contains(selectedCouponId)) {
            "$selectedCouponId 에 해당하는 쿠폰을 사용할 수 없습니다."
        }
        return products.sumOf { it.product.price * it.quantity.value } -
            discountPrice(
                products,
                selectedCouponId,
            ) + shippingPrice(products, selectedCouponId)
    }

    companion object {
        const val SHIPPING_FREE_PRICE = 0
        const val SHIPPING_PRICE = 3_000
        const val INIT_DISCOUNT_PRICE = 0
    }
}
