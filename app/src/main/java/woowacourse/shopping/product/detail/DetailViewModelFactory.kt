package woowacourse.shopping.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCatalogProductRepositoryImpl
import woowacourse.shopping.product.catalog.ProductUiModel

class DetailViewModelFactory(
    private val product: ProductUiModel,
    private val application: ShoppingApplication,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(
                product = product,
                cartProductRepository = RemoteCartProductRepositoryImpl(),
                recentlyViewedProductRepository =
                    RecentlyViewedProductRepositoryImpl(
                        ShoppingDatabase.getInstance(application).recentlyViewedProductDao(),
                        RemoteCatalogProductRepositoryImpl(),
                    ),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
