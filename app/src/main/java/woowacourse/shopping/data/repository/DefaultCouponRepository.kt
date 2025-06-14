package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.RemoteCouponDataSource
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class DefaultCouponRepository(
    private val remoteCouponDataSource: RemoteCouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> = remoteCouponDataSource.getCoupons()
}
