package woowacourse.shopping.presentation.ui.cart.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository

class SelectionViewModelFactory(private val cartRepository: CartRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SelectionViewModel(cartRepository = cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
