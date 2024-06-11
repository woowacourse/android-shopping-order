package woowacourse.shopping.data.remote.source

import woowacourse.shopping.data.mapper.CouponMapper.toCoupon
import woowacourse.shopping.data.remote.api.CouponApiService
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.domain.model.coupon.Coupon

class RemoteCouponDataSourceImpl(
    private val couponService: CouponApiService = NetworkManager.couponService(),
) : CouponDataSource {
    override suspend fun loadCoupons(): Result<List<Coupon>> {
        return runCatching {
            couponService.requestCoupons().map { it.toCoupon() }
        }
    }
}
