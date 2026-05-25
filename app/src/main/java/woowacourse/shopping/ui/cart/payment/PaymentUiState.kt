package woowacourse.shopping.ui.cart.payment

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.model.CouponOrderItem

data class PaymentUiState(
    val coupons: ImmutableList<Coupon> = persistentListOf(),
    val selectedCouponId: Long? = null,
    val orderPrice: Long = 0L,
    val orderItems: List<CouponOrderItem> = emptyList(),
    val totalQuantity: Int = 0,
    val couponDiscountPrice: Long = 0L,
    val deliveryFee: Long = 0L,
    val totalPaymentPrice: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
