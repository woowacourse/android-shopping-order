package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.remote.datasource.coupon.CouponDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getAll(): Result<List<Coupon>> {
        return couponDataSource.getAll().mapCatching {
            it.body?.map { it.toDomain() } ?: emptyList()
        }
    }
}
