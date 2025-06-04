package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.repository.CouponRepository

class DefaultCouponRepository(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): NetworkResult<List<Coupon>> =
        withContext(Dispatchers.IO) {
            couponDataSource.getCoupons()
        }
}
