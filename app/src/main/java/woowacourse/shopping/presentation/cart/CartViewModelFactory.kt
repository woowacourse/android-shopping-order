package woowacourse.shopping.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.DefaultCartRepository
import com.example.domain.repository.RecentProductRepository
import java.lang.IllegalArgumentException

class CartViewModelFactory(
    private val recommendRepository: RecentProductRepository,
    private val cartRepository: DefaultCartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(
                recommendRepository,
                cartRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
