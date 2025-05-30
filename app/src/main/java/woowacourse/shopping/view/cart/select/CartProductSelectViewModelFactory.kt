package woowacourse.shopping.view.cart.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartProductRepository

class CartProductSelectViewModelFactory(
    private val cartProductRepository: CartProductRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductSelectViewModel::class.java)) {
            return CartProductSelectViewModel(cartProductRepository) as T
        }
        throw IllegalArgumentException()
    }
}
