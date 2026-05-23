package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.domain.coupon.Coupon

interface CouponRepository {
    suspend fun getCoupons(): ApiResult<List<Coupon>>
}
