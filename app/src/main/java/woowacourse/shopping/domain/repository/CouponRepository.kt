package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.domain.model.PagingData

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>
}
