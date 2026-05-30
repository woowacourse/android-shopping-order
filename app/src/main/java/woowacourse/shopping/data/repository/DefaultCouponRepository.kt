package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.coupon.mapper.toDomain
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class DefaultCouponRepository(
    private val remoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): List<Coupon> = remoteDataSource.getCoupons().map { it.toDomain() }
}
