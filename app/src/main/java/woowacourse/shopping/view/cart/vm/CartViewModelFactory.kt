package woowacourse.shopping.view.cart.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.repository.DefaultCartRepository

class CartViewModelFactory(
    private val cartRepository: DefaultCartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(cartRepository) as T
        }
        throw IllegalArgumentException()
    }
}
