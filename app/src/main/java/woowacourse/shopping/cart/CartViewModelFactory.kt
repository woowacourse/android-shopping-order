package woowacourse.shopping.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.source.CartProductRemoteDataSource

@Suppress("UNCHECKED_CAST")
class CartViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val cartRepository: CartRepository =
                CartRepositoryImpl.Companion.initialize(
                    cartProductDataSource = CartProductRemoteDataSource(),
                )
            return CartViewModel(
                cartRepository = cartRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
