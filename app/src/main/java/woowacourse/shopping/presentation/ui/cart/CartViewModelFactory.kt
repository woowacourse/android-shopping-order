package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository

class CartViewModelFactory(
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                shoppingRepository = shoppingRepository,
                recentProductRepository = recentProductRepository,
                cartRepository = cartRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
