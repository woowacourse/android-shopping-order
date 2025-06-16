package woowacourse.shopping.feature.order

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.feature.cart.adapter.CartGoodsItem

data class OrderUiState(
    val order: List<CartGoodsItem>,
    val coupons: List<CouponState>,
    val originPayment: Payment,
    val payment: Payment,
) {
    val orderCartIds: List<Long> = order.map { it.cart.id }

    fun changeCouponCheckState(couponId: Int): OrderUiState {
        val updatedCoupons =
            coupons.map { state ->
                val isTarget = state.item.id == couponId
                state.copy(checked = isTarget && !state.checked)
            }

        val selectedCoupon = updatedCoupons.find { it.checked }
        val carts = order.map { it.cart }
        val newPayment = selectedCoupon?.item?.applyToPayment(originPayment, carts) ?: originPayment

        return copy(coupons = updatedCoupons, payment = newPayment)
    }

    companion object {
        fun of(
            order: List<CartGoodsItem>,
            coupons: List<Coupon>,
            deliveryFee: Int,
        ): OrderUiState {
            val totalPayment = order.sumOf { it.cart.product.price * it.cart.quantity }
            val originPayment = Payment(totalPayment, deliveryFee)
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
