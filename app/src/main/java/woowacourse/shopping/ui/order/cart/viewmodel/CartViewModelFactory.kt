package woowacourse.shopping.ui.order.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel

class CartViewModelFactory(
    private val cartRepository: CartRepository,
    private val orderViewModel: OrderViewModel,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                cartRepository = cartRepository,
                orderViewModel = orderViewModel,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
