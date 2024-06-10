package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.utils.toDomain
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<CouponState>> {
        return couponDataSource.getCoupons().mapCatching { dto ->
            dto.map { responseCouponDto ->
                responseCouponDto.toDomain()
            }
        }
    }
}
