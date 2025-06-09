package woowacourse.shopping.view.cart.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetPagedCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase

class CartProductRecommendViewModelFactory(
    private val selectedProducts: CartProducts,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val getPagedCartProductsUseCase: GetPagedCartProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductRecommendViewModel::class.java)) {
            return CartProductRecommendViewModel(
                selectedProducts,
                productRepository,
                recentProductRepository,
                getPagedCartProductsUseCase,
                addToCartUseCase,
                updateCartQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
