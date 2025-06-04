package woowacourse.shopping.data.repository.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun fetchAll(): Result<List<Coupon>> =
        withContext(Dispatchers.IO) {
            couponRemoteDataSource.fetchAll()
        }
}
