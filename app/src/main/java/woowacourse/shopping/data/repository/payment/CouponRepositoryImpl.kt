package woowacourse.shopping.data.repository.payment

import woowacourse.shopping.data.datasource.remote.payment.CouponRemoteDataSource
import woowacourse.shopping.data.mapper.toDomainCoupons
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun requestCoupons(): List<Coupon> =
        couponRemoteDataSource
            .requestCoupons()
            .toDomainCoupons()
}
