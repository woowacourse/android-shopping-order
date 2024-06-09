package woowacourse.shopping.domain.repository.coupon

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.coupon.Coupon

interface CouponRepository {
    suspend fun loadCoupons(): ResponseResult<List<Coupon>>
}
