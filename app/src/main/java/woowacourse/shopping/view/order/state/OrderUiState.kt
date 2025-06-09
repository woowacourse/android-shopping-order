package woowacourse.shopping.view.order.state

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.CouponApplierFactory

data class OrderUiState(
    val order: ShoppingCarts,
    val coupons: List<CouponState>,
    val originPayment: Payment,
    val payment: Payment,
) {
    val orderCartIds: List<Long>
        get() = order.cartIds

    fun changeCouponCheckState(
        couponId: Int,
        couponFactory: CouponApplierFactory,
    ): OrderUiState {
        val targetIndex = coupons.indexOfFirst { it.item.id == couponId }
        if (targetIndex == -1) return this

        val currentChecked = coupons[targetIndex].checked
        val updatedCoupons =
            coupons.mapIndexed { index, state ->
                val checked =
                    if (index == targetIndex) {
                        !state.checked
                    } else {
                        false
                    }

                state.copy(checked = checked)
            }

        val newPayment =
            if (!currentChecked) {
                val selectedCoupon = updatedCoupons[targetIndex].item
                applyCoupon(selectedCoupon, couponFactory)
            } else {
                originPayment
            }

        return copy(coupons = updatedCoupons, payment = newPayment)
    }

    private fun applyCoupon(
        coupon: Coupon,
        couponFactory: CouponApplierFactory,
    ): Payment {
        return couponFactory.apply(originPayment, order, coupon)
    }

    companion object {
        fun of(
            order: ShoppingCarts,
            coupons: List<Coupon>,
            deliveryFee: Int,
        ): OrderUiState {
            val orderPrice = order.totalPayment
            val originPayment = Payment(orderPrice, deliveryFee)
            return OrderUiState(
                order = order,
                coupons = coupons.map { CouponState(it) },
                originPayment = originPayment,
                payment = originPayment,
            )
        }
    }
}
