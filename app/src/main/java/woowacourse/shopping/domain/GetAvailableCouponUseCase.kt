package woowacourse.shopping.domain

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.CouponInfo
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.payment.BuyXGetYCoupon
import woowacourse.shopping.domain.model.payment.Coupon
import woowacourse.shopping.domain.model.payment.FixedAmountCoupon
import woowacourse.shopping.domain.model.payment.FreeShippingCoupon
import woowacourse.shopping.domain.model.payment.PercentageCoupon
import java.time.LocalDateTime

class GetAvailableCouponUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(paymentItems: PaymentItems): List<CouponInfo> {
        orderRepository.loadCoupons()
        val now = LocalDateTime.now()
        return orderRepository.coupons.value
            .filter { it.isAvailable(paymentItems, now) }
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

    private fun Coupon.isAvailable(
        paymentItems: PaymentItems,
        now: LocalDateTime,
    ): Boolean {
        if (expirationDate < now.toLocalDate()) return false
        return when (this) {
            is FixedAmountCoupon -> paymentItems.totalPrice >= minimumAmount
            is FreeShippingCoupon -> paymentItems.totalPrice >= minimumAmount
            is BuyXGetYCoupon ->
                paymentItems.getItems().any { it.quantity >= buyQuantity + freeGetQuantity }
            is PercentageCoupon -> {
                val start = availableTimeStart
                val end = availableTimeEnd
                start == null ||
                    end == null ||
                    (now.toLocalTime() in start..end)
            }
            else -> true
        }
    }
}
