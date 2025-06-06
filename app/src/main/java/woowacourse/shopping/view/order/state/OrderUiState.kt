package woowacourse.shopping.view.order.state

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.coupon.BogoCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.CouponApplierFactory
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeshippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon

data class OrderUiState(
    val order: List<ShoppingCart>,
    val coupons: List<CouponState>,
    val originPayment: Payment,
    val payment: Payment,
) {
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
        return when (coupon) {
            is BogoCoupon -> couponFactory.bogoCouponApplier.apply(originPayment, order, coupon)
            is FixedCoupon -> couponFactory.fixedCouponApplier.apply(originPayment, coupon)
            is FreeshippingCoupon -> couponFactory.freeShippingCouponApplier.apply(originPayment)
            is MiracleSaleCoupon -> couponFactory.miracleSaleCouponApplier.apply(originPayment, coupon)
        }
    }

    companion object {
        fun of(
            order: List<ShoppingCart>,
            coupons: List<Coupon>,
            deliveryFee: Int,
        ): OrderUiState {
            val orderPrice = order.sumOf { it.payment }
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
