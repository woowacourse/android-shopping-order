package woowacourse.shopping.product.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCatalogProductRepositoryImpl

class CatalogViewModelFactory(
    private val application: ShoppingApplication,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(
                // TODO cartProductRepository 하나로 개선
                cartProductRepository =
                    RemoteCartProductRepositoryImpl(),
                recentlyViewedProductRepository =
                    RecentlyViewedProductRepositoryImpl(
                        ShoppingDatabase.getInstance(application).recentlyViewedProductDao(),
                        RemoteCatalogProductRepositoryImpl(),
                    ),
                catalogProductRepository = RemoteCatalogProductRepositoryImpl(),
                remoteCatalogProductRepository = RemoteCatalogProductRepositoryImpl(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
