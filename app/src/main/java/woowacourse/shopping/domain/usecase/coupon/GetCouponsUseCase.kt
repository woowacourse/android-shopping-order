package woowacourse.shopping.domain.usecase.coupon

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class GetCouponsUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(): Result<List<Coupon>> = couponRepository.getCoupons()
}
