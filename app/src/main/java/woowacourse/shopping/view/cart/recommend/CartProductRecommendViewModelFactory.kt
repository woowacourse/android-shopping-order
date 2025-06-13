package woowacourse.shopping.view.cart.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetRecommendedProductsUseCase

class CartProductRecommendViewModelFactory(
    private val selectedProducts: CartProducts,
    private val getRecommendedProductsUseCase: GetRecommendedProductsUseCase,
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductRecommendViewModel::class.java)) {
            return CartProductRecommendViewModel(
                selectedProducts,
                getRecommendedProductsUseCase,
                getCartProductsUseCase,
                addToCartUseCase,
                updateCartQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
