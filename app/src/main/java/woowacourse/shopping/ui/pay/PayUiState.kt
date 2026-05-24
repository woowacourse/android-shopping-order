package woowacourse.shopping.ui.pay

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PayUiState(
    val coupons: ImmutableList<CouponUiModel> = persistentListOf(),
    val selectedCouponId: String? = null,
    val totalOrderPrice: Long = 0,
    val discountAmount: Long = 0,
    val shippingFee: Long = 3000,
    val finalPrice: Long = 3000,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
