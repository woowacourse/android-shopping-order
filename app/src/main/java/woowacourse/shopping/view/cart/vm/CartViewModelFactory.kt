package woowacourse.shopping.view.cart.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.view.loader.HistoryLoader

class CartViewModelFactory(
    private val cartRepository: CartRepository,
    private val historyLoader: HistoryLoader,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(cartRepository, historyLoader) as T
        }
        throw IllegalArgumentException()
    }
}
