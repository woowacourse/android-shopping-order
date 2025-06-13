package woowacourse.shopping.data.datasource.coupon

import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.model.response.CouponsResponse

interface CouponRemoteDataSource {
    suspend fun fetchAllCoupons(): ApiResult<CouponsResponse>
}
