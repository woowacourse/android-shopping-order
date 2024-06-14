package woowacourse.shopping

import woowacourse.shopping.TestFixture.bogoCoupon
import woowacourse.shopping.TestFixture.fixed5000DiscountCoupon
import woowacourse.shopping.TestFixture.freeShippingCoupon
import woowacourse.shopping.TestFixture.notNowTimeBasedDiscountCoupon
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.OrderRepository

class TestOrderRepository : OrderRepository {
    override suspend fun orderShoppingCart(ids: List<Long>): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getCoupons(): Result<List<Coupon>> {
        return Result.success(listOf(fixed5000DiscountCoupon, bogoCoupon, freeShippingCoupon, notNowTimeBasedDiscountCoupon))
    }
}
