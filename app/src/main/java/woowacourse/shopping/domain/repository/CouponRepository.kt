package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>

    suspend fun getAvailableCoupons(cartItems: List<CartItem>): Result<List<Coupon>>
}
