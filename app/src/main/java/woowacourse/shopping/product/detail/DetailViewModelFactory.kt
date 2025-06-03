package woowacourse.shopping.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.product.catalog.ProductUiModel

class DetailViewModelFactory(
    private val product: ProductUiModel,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(
                product = product,
                cartProductRepository = RepositoryProvider.provideCartProductRepository(),
                recentlyViewedProductRepository = RepositoryProvider.provideRecentlyViewedProductRepository(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
