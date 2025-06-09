package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class GetAvailableCouponsUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(cartItems: List<CartItem>): Result<List<Coupon>> =
        couponRepository.getCoupons().mapCatching { coupons ->
            coupons.filter { coupon -> coupon.isAvailable(cartItems) }
        }
}
