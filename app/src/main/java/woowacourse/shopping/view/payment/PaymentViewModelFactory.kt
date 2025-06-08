package woowacourse.shopping.view.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModelFactory(
    private val selectedProducts: List<CartProduct>,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(selectedProducts, couponRepository, orderRepository) as T
        }
        throw IllegalArgumentException()
    }
}
