package woowacourse.shopping.view.product.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetPagedCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.GetTotalCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetPagedProductsUseCase

class ProductCatalogViewModelFactory(
    private val recentProductRepository: RecentProductRepository,
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val getPagedCartProductsUseCase: GetPagedCartProductsUseCase,
    private val getTotalCartProductQuantityUseCase: GetTotalCartProductQuantityUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductCatalogViewModel::class.java)) {
            return ProductCatalogViewModel(
                recentProductRepository,
                getPagedProductsUseCase,
                getPagedCartProductsUseCase,
                getTotalCartProductQuantityUseCase,
                addToCartUseCase,
                updateCartQuantityUseCase,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
