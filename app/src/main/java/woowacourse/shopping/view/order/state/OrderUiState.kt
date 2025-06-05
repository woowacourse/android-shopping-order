package woowacourse.shopping.view.order.state

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.coupon.Coupon

data class OrderUiState(
    val coupons: List<CouponState>,
    val payment: Payment,
) {
    fun changeCouponCheckState(couponId: Int): OrderUiState {
        val targetIndex = coupons.indexOfFirst { it.item.id == couponId }
        if (targetIndex == -1) return this

        val mutableCoupons = coupons.toMutableList()
        mutableCoupons[targetIndex] = mutableCoupons[targetIndex].toggleChecked()

        return copy(coupons = mutableCoupons)
    }

    companion object {
        fun of(coupons: List<Coupon>) =
            OrderUiState(
                coupons = coupons.map { CouponState(it) },
                payment = Payment(0, 0, 0, 0),
            )
    }
}
