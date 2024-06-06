package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.CouponDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val dataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<CouponState>> = dataSource.getCoupons().map { it.toDomain() }
}
