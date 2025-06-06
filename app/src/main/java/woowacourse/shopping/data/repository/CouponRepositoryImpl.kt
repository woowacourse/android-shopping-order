package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.model.coupon.toDomain
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val dataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun fetchCoupons(): Result<List<Coupon>> =
        runCatchingDebugLog {
            val response = dataSource.fetchCoupons().getOrDefault(emptyList())
            response.map { it.toDomain() }
        }
}
