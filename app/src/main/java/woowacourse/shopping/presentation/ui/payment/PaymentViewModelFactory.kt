package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModelFactory(private val orderRepository: OrderRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class : $modelClass")
    }
}
