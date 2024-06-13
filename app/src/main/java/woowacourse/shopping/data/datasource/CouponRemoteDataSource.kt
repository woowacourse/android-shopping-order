package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.response.ResponseCouponDto

interface CouponRemoteDataSource {
    suspend fun getCoupons(): Result<List<ResponseCouponDto>>
}
