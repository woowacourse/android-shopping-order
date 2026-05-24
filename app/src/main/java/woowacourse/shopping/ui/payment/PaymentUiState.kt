package woowacourse.shopping.ui.payment

import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Discount
import java.time.LocalDateTime

data class PaymentUiState(
    val coupons: List<Coupon> = emptyList(),
    val order: Order = Order(emptyList(), currentTime = LocalDateTime.now(), isRemoteArea = false),
    val selectedCoupon: Coupon? = null,
    val discount: Discount = Discount(),
    val isLoading: Boolean = false,
)
