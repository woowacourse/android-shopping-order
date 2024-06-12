package woowacourse.shopping.data.remote.datasource.coupon

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.response.CouponResponse
import woowacourse.shopping.data.remote.service.CouponApi

class RetrofitCouponDataSource(
    private val couponApi: CouponApi = CouponApi.service(),
) : CouponDataSource {
    override suspend fun getAll(): Response<List<CouponResponse>> {
        return couponApi.getCoupons()
    }
}
