package woowacourse.shopping.view.cart.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.usecase.UpdateQuantityUseCase

class CartProductSelectViewModelFactory(
    private val cartProductRepository: CartProductRepository,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductSelectViewModel::class.java)) {
            return CartProductSelectViewModel(cartProductRepository, updateQuantityUseCase) as T
        }
        throw IllegalArgumentException()
    }
}
