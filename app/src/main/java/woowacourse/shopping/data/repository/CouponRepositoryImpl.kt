package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSource
import woowacourse.shopping.data.mapper.CouponMapper
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getAvailableCoupons(): List<Coupon> =
        try {
            couponRemoteDataSource.getCoupons().map { CouponMapper.toDomain(it) }
        } catch (_: Exception) {
            emptyList()
        }
}
