package woowacourse.shopping.product.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryProvider

class CatalogViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(
                cartProductRepository = RepositoryProvider.provideCartProductRepository(),
                recentlyViewedProductRepository = RepositoryProvider.provideRecentlyViewedProductRepository(),
                catalogProductRepository = RepositoryProvider.provideCatalogProductRepository(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
