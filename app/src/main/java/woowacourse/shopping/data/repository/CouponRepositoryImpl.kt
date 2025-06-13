package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val remoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        remoteDataSource.getCoupons().mapCatching { dtos ->
            dtos.map { it.toCoupon() }
        }
}
