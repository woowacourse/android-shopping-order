package woowacourse.shopping.view.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.usecase.coupon.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.payment.OrderProductsUseCase

class PaymentViewModelFactory(
    private val cartProducts: CartProducts,
    private val getCouponsUseCase: GetCouponsUseCase,
    private val orderProductsUseCase: OrderProductsUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(cartProducts, getCouponsUseCase, orderProductsUseCase) as T
        }
        throw IllegalArgumentException()
    }
}
