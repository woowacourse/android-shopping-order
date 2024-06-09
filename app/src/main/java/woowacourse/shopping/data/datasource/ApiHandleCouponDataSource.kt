package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.dto.response.CouponDto

interface ApiHandleCouponDataSource {
    suspend fun getCoupons(): ApiResponse<List<CouponDto>>
}
