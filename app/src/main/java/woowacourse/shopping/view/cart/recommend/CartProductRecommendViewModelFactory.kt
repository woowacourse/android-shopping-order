package woowacourse.shopping.view.cart.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.UpdateQuantityUseCase

class CartProductRecommendViewModelFactory(
    private val selectedProducts: CartProducts,
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductRecommendViewModel::class.java)) {
            return CartProductRecommendViewModel(
                selectedProducts,
                productRepository,
                cartProductRepository,
                recentProductRepository,
                addToCartUseCase,
                updateQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
