package woowacourse.shopping.presentation.ui.cart.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.repository.CartRepository

class SelectionViewModelFactory(
    private val cartRepository: CartRepository,
    private val orderDatabase: OrderDatabase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SelectionViewModel(
                cartRepository = cartRepository,
                orderDatabase = orderDatabase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
