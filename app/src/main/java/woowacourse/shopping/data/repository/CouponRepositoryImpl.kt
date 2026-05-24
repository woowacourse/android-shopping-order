package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.coupon.Coupons
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val dataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Coupons {
        val result = dataSource.getCoupons()
        return result.toDomain()
    }
}
