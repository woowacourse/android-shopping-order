package woowacourse.shopping.presentation.ui.payment

import woowacourse.shopping.presentation.model.CartUiModel
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toDomain

data class PaymentUiState(
    val coupons: List<CouponUiModel> = emptyList(),
    val orderCarts: List<CartUiModel> = emptyList(),
    val checkedCouponId: Int = -1,
) {
    val orderTotalPrice get() = orderCarts.sumOf { it.totalPrice }

    val couponDiscountPrice
        get() =
            checkedCoupon?.calculateDiscount(orderTotalPrice, orderCarts.map { it.toDomain() })
                ?: 0

    private val checkedCoupon: CouponUiModel?
        get() = coupons.find { it.id == checkedCouponId }

    val paymentTotalPrice get() = orderCarts.sumOf { it.totalPrice } + DELIVERY_FEE_AMOUNT - couponDiscountPrice

    companion object {
        const val DELIVERY_FEE_AMOUNT = 3_000
    }
}
