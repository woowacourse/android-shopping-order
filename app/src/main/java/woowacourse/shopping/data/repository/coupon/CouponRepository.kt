package woowacourse.shopping.data.repository.coupon

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.Coupon

interface CouponRepository {
    val coupons: StateFlow<List<Coupon>>

    suspend fun refreshCoupons()
}
