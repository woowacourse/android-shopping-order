package woowacourse.shopping.fixture

import woowacourse.shopping.data.util.TimeProvider
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponPolicyContext
import woowacourse.shopping.domain.model.CouponType
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CouponRepository

class FakeCouponRepository(
    private val coupons: List<Coupon>,
    private val timeProvider: TimeProvider,
) : CouponRepository {
    override suspend fun getCoupons(
        totalAmount: Long,
        orderProducts: List<Product>,
    ): Result<List<Coupon>> {
        val filteredCoupons =
            coupons.filter { coupon ->
                val policy = CouponType.from(coupon.code).getPolicy()
                val policyContext =
                    CouponPolicyContext(
                        totalAmount = totalAmount,
                        orderProducts = orderProducts,
                        currentDateTime = timeProvider.currentTime(),
                    )
                policy.isApplicable(coupon, policyContext)
            }

        return Result.success(filteredCoupons)
    }
}
