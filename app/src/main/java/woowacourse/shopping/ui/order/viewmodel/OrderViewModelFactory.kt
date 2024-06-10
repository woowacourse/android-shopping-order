package woowacourse.shopping.ui.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository

class OrderViewModelFactory(private val cartRepository: CartRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(cartRepository = cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
