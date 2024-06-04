package woowacourse.shopping.presentation.ui.cart.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository

class RecommendViewModelFactory(
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendViewModel(
                cartRepository = cartRepository,
                shoppingRepository = shoppingRepository,
                recentProductRepository = recentProductRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
