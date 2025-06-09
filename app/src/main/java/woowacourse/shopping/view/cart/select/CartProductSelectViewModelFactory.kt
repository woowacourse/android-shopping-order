package woowacourse.shopping.view.cart.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.usecase.GetPagedCartProductsUseCase
import woowacourse.shopping.domain.usecase.RemoveFromCartUseCase
import woowacourse.shopping.domain.usecase.UpdateQuantityUseCase

class CartProductSelectViewModelFactory(
    private val getPagedCartProductsUseCase: GetPagedCartProductsUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductSelectViewModel::class.java)) {
            return CartProductSelectViewModel(
                getPagedCartProductsUseCase,
                removeFromCartUseCase,
                updateQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
