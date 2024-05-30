package woowacourse.shopping.view.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.domain.repository.OrderRepository

class CartViewModelFactory(
    private val cartRepository: CartRepository2,
    private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                cartRepository = cartRepository,
                orderRepository = orderRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
