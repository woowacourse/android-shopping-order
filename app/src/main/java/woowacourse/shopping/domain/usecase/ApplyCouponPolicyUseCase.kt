package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponType
import woowacourse.shopping.domain.model.OrderAdjustment
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.policy.CouponPolicy

class ApplyCouponPolicyUseCase {
    fun applyPolicy(
        coupon: Coupon,
        initialOrderPrice: Long,
        checkedItems: List<Product>,
    ): OrderAdjustment {
        val policy = CouponType.Companion.from(coupon.code).getPolicy()
        val discount = coupon.discount ?: 0L
        val discountPercentage = (coupon.discount ?: 0L).toDouble() / 100.0

        return when (policy) {
            CouponPolicy.Fixed5000 -> OrderAdjustment(discount = discount)
            CouponPolicy.MiracleSale ->
                OrderAdjustment(
                    discount = (initialOrderPrice * discountPercentage).toLong(),
                )
            CouponPolicy.FreeShipping -> OrderAdjustment(deliveryCharge = 0)
            CouponPolicy.Bogo -> {
                val discount = calculateBogoDiscount(coupon, checkedItems)
                OrderAdjustment(discount = discount)
            }
            CouponPolicy.Default -> OrderAdjustment()
        }
    }

    private fun calculateBogoDiscount(
        coupon: Coupon,
        checkedItems: List<Product>,
    ): Long {
        val targetProduct =
            checkedItems
                .filter { it.quantity >= (coupon.buyQuantity ?: 0) }
                .maxByOrNull { it.price }

        val buyQuantity = coupon.buyQuantity ?: 0
        val getQuantity = coupon.getQuantity ?: 0

        return if (targetProduct != null && buyQuantity > 0 && getQuantity > 0) {
            val discountQuantity = getQuantity
            discountQuantity * targetProduct.price.toLong()
        } else {
            0L
        }
    }
}
