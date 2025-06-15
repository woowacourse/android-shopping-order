package woowacourse.shopping.data.source.remote.coupon

import woowacourse.shopping.data.model.coupon.CouponResponse
import woowacourse.shopping.data.source.remote.api.CouponApiService
import woowacourse.shopping.data.source.remote.util.safeApiCall

class CouponRemoteDataSource(
    private val api: CouponApiService,
) : CouponDataSource {
    override suspend fun getCoupons(): Result<List<CouponResponse>> =
        safeApiCall {
            api.getCoupons()
        }
}
