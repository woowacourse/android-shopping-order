package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class GetAvailableCouponUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(orderPrice: Int): Result<List<Coupon>> =
        couponRepository
            .fetchAll()
            .mapCatching { coupons ->
                coupons.filter { coupon -> coupon.isValidForOrder(orderPrice) }
            }.onFailure { throwable -> throw throwable }
}
