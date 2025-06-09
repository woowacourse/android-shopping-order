package woowacourse.shopping.ui.payment

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Products.Companion.EMPTY_PRODUCTS
import woowacourse.shopping.domain.usecase.CalculatePaymentAmountByCouponUseCase.Companion.DEFAULT_SHIPPING_FEE
import woowacourse.shopping.ui.payment.adapter.CouponUiModel

data class PaymentUiState(
    val coupons: List<CouponUiModel> = emptyList(),
    val selectedProducts: Products = EMPTY_PRODUCTS,
    val orderPrice: Int = 0,
    val couponDiscount: Int = 0,
    val deliveryPrice: Int = DEFAULT_SHIPPING_FEE,
    val totalPaymentAmount: Int = 0,
)
