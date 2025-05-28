package woowacourse.shopping.view.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

class ShoppingCartViewModelFactory(
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)) {
            return ShoppingCartViewModel(cartProductRepository, recentProductRepository, productRepository) as T
        }
        throw IllegalArgumentException()
    }
}
