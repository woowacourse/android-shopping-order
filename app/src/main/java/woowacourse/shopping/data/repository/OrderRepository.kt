package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.dto.coupon.response.CouponResponse
import woowacourse.shopping.error.Error
import woowacourse.shopping.error.Result

interface OrderRepository {
    suspend fun getCoupons(): Result<List<CouponResponse>, Error>
}
