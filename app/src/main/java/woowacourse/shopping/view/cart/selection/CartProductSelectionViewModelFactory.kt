package woowacourse.shopping.view.cart.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartProductRepository

class CartProductSelectionViewModelFactory(
    private val cartProductRepository: CartProductRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductSelectionViewModel::class.java)) {
            return CartProductSelectionViewModel(cartProductRepository) as T
        }
        throw IllegalArgumentException()
    }
}
