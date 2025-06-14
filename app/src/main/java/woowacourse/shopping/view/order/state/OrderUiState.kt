package woowacourse.shopping.view.order.state

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.domain.coupon.Coupon

data class OrderUiState(
    val order: ShoppingCarts,
    val coupons: List<CouponState>,
    val originPayment: Payment,
    val payment: Payment,
) {
    val orderCartIds: List<Long> = order.cartIds

    fun changeCouponCheckState(couponId: Int): OrderUiState {
        val updatedCoupons =
            coupons.map { state ->
                val isTarget = state.item.id == couponId
                state.copy(checked = isTarget && !state.checked)
            }

        val selectedCoupon = updatedCoupons.find { it.checked }
        val newPayment = selectedCoupon?.item?.applyToPayment(originPayment, order) ?: originPayment

        return copy(coupons = updatedCoupons, payment = newPayment)
    }

    companion object {
        fun of(
            order: ShoppingCarts,
            coupons: List<Coupon>,
            deliveryFee: Int,
        ): OrderUiState {
            val originPayment = Payment(order.totalPayment, deliveryFee)
            val couponStates = coupons.map { CouponState(it) }

            return OrderUiState(
                order = order,
                coupons = couponStates,
                originPayment = originPayment,
                payment = originPayment,
            )
        }
    }
}
