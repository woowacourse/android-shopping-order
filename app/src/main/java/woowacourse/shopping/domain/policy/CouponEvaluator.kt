package woowacourse.shopping.domain.policy

import woowacourse.shopping.data.util.TimeProvider
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponPolicyContext
import woowacourse.shopping.domain.model.CouponType
import woowacourse.shopping.domain.model.Product

class CouponEvaluator(
    private val timeProvider: TimeProvider,
) {
    fun evaluateApplicableCoupons(
        coupons: List<Coupon>,
        totalAmount: Long,
        orderProducts: List<Product>,
    ): List<Coupon> {
        val context =
            CouponPolicyContext(
                totalAmount = totalAmount,
                orderProducts = orderProducts,
                currentDateTime = timeProvider.currentTime(),
            )

        return coupons.filter { coupon ->
            val policy = CouponType.from(coupon.code).getPolicy()
            policy.isApplicable(coupon, context)
        }
    }
}
