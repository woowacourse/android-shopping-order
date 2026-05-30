package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalDateTime

class GetAvailableCouponsUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(items: PaymentItems): List<Coupon> =
        couponRepository.getCoupons().filter {
            it.isApplicable(items, LocalDateTime.now())
        }
}
