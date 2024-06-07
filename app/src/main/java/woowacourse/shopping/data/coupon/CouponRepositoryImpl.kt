package woowacourse.shopping.data.coupon

import woowacourse.shopping.data.datasource.CouponRemoteDataSource

class CouponRepositoryImpl(
    private val couponDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<CouponState>> {
        return couponDataSource.getCoupons().mapCatching {
            it.map { responseCouponDto ->
                responseCouponDto.toDomain()
            }
        }
    }
}
