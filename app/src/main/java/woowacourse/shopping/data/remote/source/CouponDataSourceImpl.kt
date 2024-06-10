package woowacourse.shopping.data.remote.source

import woowacourse.shopping.data.remote.api.ApiClient
import woowacourse.shopping.data.remote.api.CouponApiService
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.utils.CouponMapper.toCoupon

class CouponDataSourceImpl(apiClient: ApiClient) : CouponDataSource {
    private val couponApiService: CouponApiService =
        apiClient.createService(CouponApiService::class.java)

    override suspend fun loadCoupons(): Result<List<Coupon>> {
        return runCatching {
            couponApiService.requestCoupons().map { it.toCoupon() }
        }
    }
}
