package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.coupon.CouponResponse
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.data.util.safeApiCall

class CouponRemoteDataSourceImpl(
    private val service: CouponService,
) : CouponRemoteDataSource {
    override suspend fun fetchCoupons(): Result<List<CouponResponse>> =
        safeApiCall {
            service.fetchCoupons()
        }
}
