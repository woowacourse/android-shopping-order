package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.RemoteCouponDataSource
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class DefaultCouponRepository(
    private val remoteCouponDataSource: RemoteCouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        withContext(Dispatchers.IO) {
            remoteCouponDataSource.getCoupons()
        }
}
