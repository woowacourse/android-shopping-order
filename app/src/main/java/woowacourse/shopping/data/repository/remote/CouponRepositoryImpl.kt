package woowacourse.shopping.data.repository.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.common.mapper.toDomain
import woowacourse.shopping.data.datasource.remote.CouponDataSource
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        withContext(defaultDispatcher) {
            runCatching {
                couponDataSource.getCoupons().map { it.toDomain() }
            }
        }
}
