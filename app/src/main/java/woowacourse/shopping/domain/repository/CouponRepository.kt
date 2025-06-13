package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.coupon.Coupons
import java.time.LocalTime

interface CouponRepository {
    suspend fun fetchFilteredCoupons(
        cartProducts: List<CartProduct>,
        time: LocalTime,
    ): Result<Coupons>
}
