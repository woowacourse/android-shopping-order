package woowacourse.shopping.data.remote.datasource.coupon

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.response.CouponResponse

interface CouponDataSource {

    suspend fun getCoupons(): Response<List<CouponResponse>>
}