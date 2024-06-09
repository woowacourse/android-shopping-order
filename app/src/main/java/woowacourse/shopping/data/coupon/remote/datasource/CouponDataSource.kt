package woowacourse.shopping.data.coupon.remote.datasource

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.coupon.remote.dto.CouponDto

interface CouponDataSource {
    suspend fun loadCoupons(): ResponseResult<List<CouponDto>>
}