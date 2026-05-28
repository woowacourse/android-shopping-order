package woowacourse.shopping.ui.pay

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.model.PaymentPrice

data class PayUiState(
    val coupons: ImmutableList<CouponUiModel> = persistentListOf(),
    val selectedCouponId: String? = null,
    val totalOrderPrice: Long = 0,
    val discountAmount: Long = 0,
    val shippingFee: Long = PaymentPrice.DEFAULT_SHIPPING_FEE.amount,
    val finalPrice: Long = PaymentPrice.DEFAULT_SHIPPING_FEE.amount,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
