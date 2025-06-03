package woowacourse.shopping.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryModule
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.DecreaseProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IncreaseProductQuantityUseCase

class ProductViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val cartRepository = RepositoryModule.provideCartRepository()
        val productRepository = RepositoryModule.provideProductRepository()
        val recentProductRepository = RepositoryModule.provideRecentProductRepository()
        val increaseProductQuantityUseCase = IncreaseProductQuantityUseCase(cartRepository)
        val decreaseProductQuantityUseCase = DecreaseProductQuantityUseCase(cartRepository)
        val addToCartUseCase = AddToCartUseCase(cartRepository)
        return ProductViewModel(
            cartRepository,
            productRepository,
            recentProductRepository,
            increaseProductQuantityUseCase,
            decreaseProductQuantityUseCase,
            addToCartUseCase,
        ) as T
    }
}
