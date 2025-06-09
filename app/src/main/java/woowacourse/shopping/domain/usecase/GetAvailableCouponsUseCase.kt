package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.CouponContext
import woowacourse.shopping.domain.repository.CouponRepository

class GetAvailableCouponsUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(cartItems: List<CartItem>): Result<List<CouponContext>> =
        couponRepository.getCoupons().mapCatching { coupons ->
            coupons
                .filter { coupon -> coupon.isAvailable(cartItems) }
                .map { CouponContext(it, cartItems) }
        }
}
