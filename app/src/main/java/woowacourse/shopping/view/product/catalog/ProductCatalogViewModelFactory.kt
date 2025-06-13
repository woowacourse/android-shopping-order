package woowacourse.shopping.view.product.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.GetTotalCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetProductsUseCase
import woowacourse.shopping.domain.usecase.product.GetRecentProductsUseCase

class ProductCatalogViewModelFactory(
    private val getRecentProductsUseCase: GetRecentProductsUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getCartProductsUseCase: GetCartProductsUseCase,
    private val getTotalCartProductQuantityUseCase: GetTotalCartProductQuantityUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductCatalogViewModel::class.java)) {
            return ProductCatalogViewModel(
                getRecentProductsUseCase,
                getProductsUseCase,
                getCartProductsUseCase,
                getTotalCartProductQuantityUseCase,
                addToCartUseCase,
                updateCartQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
