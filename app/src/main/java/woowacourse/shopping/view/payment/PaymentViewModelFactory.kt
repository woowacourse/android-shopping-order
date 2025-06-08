package woowacourse.shopping.view.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModelFactory(
    private val cartProducts: CartProducts,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(cartProducts, couponRepository, orderRepository) as T
        }
        throw IllegalArgumentException()
    }
}
