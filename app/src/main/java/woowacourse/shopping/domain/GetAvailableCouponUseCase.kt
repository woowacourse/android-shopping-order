package woowacourse.shopping.domain

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.CouponInfo
import woowacourse.shopping.domain.model.payment.BuyXGetYCoupon
import woowacourse.shopping.domain.model.payment.Coupon
import woowacourse.shopping.domain.model.payment.FixedAmountCoupon
import woowacourse.shopping.domain.model.payment.FreeShippingCoupon
import woowacourse.shopping.domain.model.payment.Order
import woowacourse.shopping.domain.model.payment.PercentageCoupon

class GetAvailableCouponUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(order: Order): List<CouponInfo> {
        orderRepository.loadCoupons()
        return orderRepository.coupons.value
            .filter { it.isApplicable(order) }
            .map { it.toCouponInfo() }
    }

    private fun Coupon.toCouponInfo(): CouponInfo =
        when (this) {
            is FixedAmountCoupon -> CouponInfo.Fixed(code, expirationDate, minimumAmount, discountAmount)
            is PercentageCoupon -> CouponInfo.Percentage(code, expirationDate, discountRate = discountRate)
            is BuyXGetYCoupon -> CouponInfo.BuyXGetY(code, expirationDate, buyQuantity = buyQuantity, freeGetQuantity = freeGetQuantity)
            is FreeShippingCoupon -> CouponInfo.FreeShipping(code, expirationDate, minimumAmount)
            else -> CouponInfo.FreeShipping(code, expirationDate, 0L)
        }
}
