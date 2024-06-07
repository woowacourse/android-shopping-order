package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.remote.api.CouponService
import woowacourse.shopping.remote.mapper.toDomain

class CouponRemoteDataSourceImpl(private val couponService: CouponService) : CouponRemoteDataSource {
    override suspend fun getCoupons(): List<Coupon> = couponService.getCoupons().map { it.toDomain() }
}
