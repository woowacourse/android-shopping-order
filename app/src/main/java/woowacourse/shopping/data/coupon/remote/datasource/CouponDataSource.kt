package woowacourse.shopping.data.coupon.remote.datasource

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.coupon.remote.dto.CouponResponse

interface CouponDataSource {
    suspend fun loadCoupons(): ResponseResult<CouponResponse>
}