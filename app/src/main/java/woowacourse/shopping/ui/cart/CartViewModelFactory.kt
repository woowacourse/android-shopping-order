package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import java.lang.IllegalArgumentException

class CartViewModelFactory(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(productRepository, cartRepository) as T
        }
        throw IllegalArgumentException()
    }
}
