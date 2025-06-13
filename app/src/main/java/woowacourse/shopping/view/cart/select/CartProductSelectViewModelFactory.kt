package woowacourse.shopping.view.cart.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.usecase.cart.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.RemoveFromCartUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase

class CartProductSelectViewModelFactory(
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductSelectViewModel::class.java)) {
            return CartProductSelectViewModel(
                getCartProductsUseCase,
                removeFromCartUseCase,
                updateCartQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
