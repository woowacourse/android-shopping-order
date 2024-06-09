package woowacourse.shopping.data.payment.remote.datasource

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.payment.remote.dto.CouponResponse

interface CouponDataSource {
    suspend fun loadCoupons(): ResponseResult<CouponResponse>
}