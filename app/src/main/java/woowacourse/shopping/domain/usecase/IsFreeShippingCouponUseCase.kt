package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class IsFreeShippingCouponUseCase(
    private val couponRepository: CouponRepository,
) {
    operator fun invoke(couponId: Long): Boolean {
        val selectedCoupon = couponRepository.fetchCoupon(couponId) ?: return false

        return selectedCoupon is Coupon.FreeShippingOver
    }
}
