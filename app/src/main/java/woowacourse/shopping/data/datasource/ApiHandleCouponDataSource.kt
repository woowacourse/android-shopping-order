package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.dto.response.CouponDto

interface ApiHandleCouponDataSource {
    suspend fun getCoupons(): ApiResult<List<CouponDto>>

}
