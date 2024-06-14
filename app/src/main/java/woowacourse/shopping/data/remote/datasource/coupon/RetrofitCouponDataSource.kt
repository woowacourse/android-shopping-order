package woowacourse.shopping.data.remote.datasource.coupon

import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.response.CouponResponse
import woowacourse.shopping.data.remote.service.CouponApi

class RetrofitCouponDataSource(
    private val couponApi: CouponApi = CouponApi.service(),
) : CouponDataSource {
    override suspend fun getAll(): Result<Message<List<CouponResponse>>> =
        runCatching {
            val response = couponApi.getCoupons()
            Message(
                code = response.code(),
                body = response.body(),
            )
        }
}
