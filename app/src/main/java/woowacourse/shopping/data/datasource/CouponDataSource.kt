package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.dto.response.CouponDto

interface CouponDataSource {
    suspend fun getCoupons(): ApiResponse<List<CouponDto>>
}
