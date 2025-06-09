package woowacourse.shopping.view.cart.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetPagedCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetPagedProductsUseCase

class CartProductRecommendViewModelFactory(
    private val selectedProducts: CartProducts,
    private val recentProductRepository: RecentProductRepository,
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val getPagedCartProductsUseCase: GetPagedCartProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductRecommendViewModel::class.java)) {
            return CartProductRecommendViewModel(
                selectedProducts,
                recentProductRepository,
                getPagedProductsUseCase,
                getPagedCartProductsUseCase,
                addToCartUseCase,
                updateCartQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
