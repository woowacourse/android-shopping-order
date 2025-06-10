package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.coupon.CouponResponse
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.data.util.safeApiCall

class CouponRemoteDataSourceImpl(
    private val couponService: CouponService,
) : CouponRemoteDataSource {
    override suspend fun fetchCoupons(): Result<List<CouponResponse>> =
        safeApiCall {
            couponService.fetchCoupons()
        }
}
