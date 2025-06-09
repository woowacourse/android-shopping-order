package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.dto.response.coupon.toDomain
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val remoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        return remoteDataSource.getCoupons()
            .mapCatching { coupons -> coupons.map { it.toDomain() } }
    }
}
