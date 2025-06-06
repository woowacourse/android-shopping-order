package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class GetAvailableCouponUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(
        orderPrice: Int,
        items: List<CartItem>,
    ): Result<List<Coupon>> =
        couponRepository
            .fetchAll()
            .mapCatching { coupons ->
                coupons.filter { coupon -> coupon.isValidForOrder(orderPrice, items) }
            }.onFailure { throwable -> throw throwable }
}
