package woowacourse.shopping.view.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.product.GetRecentProductsUseCase
import woowacourse.shopping.domain.usecase.product.SaveRecentlyViewedProductUseCase

class ProductDetailViewModelFactory(
    private val product: Product,
    private val getRecentProductsUseCase: GetRecentProductsUseCase,
    private val saveRecentlyViewedProductUseCase: SaveRecentlyViewedProductUseCase,
    private val addToCartUseCase: AddToCartUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            return ProductDetailViewModel(
                product,
                getRecentProductsUseCase,
                saveRecentlyViewedProductUseCase,
                addToCartUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
