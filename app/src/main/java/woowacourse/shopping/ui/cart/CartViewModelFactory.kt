package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.product.remote.retrofit.RemoteProductRepository
import java.lang.IllegalArgumentException

class CartViewModelFactory(
    private val productRepository: RemoteProductRepository,
    private val cartRepository: RemoteCartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(productRepository, cartRepository) as T
        }
        throw IllegalArgumentException()
    }
}
