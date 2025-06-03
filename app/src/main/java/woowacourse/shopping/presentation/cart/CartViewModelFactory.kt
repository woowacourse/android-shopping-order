package woowacourse.shopping.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase

class CartViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val cartRepository = RepositoryModule.provideCartRepository()
        val increaseProductQuantityUseCase = IncreaseProductQuantityUseCase(cartRepository)
        val decreaseProductQuantityUseCase = DecreaseProductQuantityUseCase(cartRepository)
        return CartViewModel(
            cartRepository,
            increaseProductQuantityUseCase,
            decreaseProductQuantityUseCase,
        ) as T
    }
}
