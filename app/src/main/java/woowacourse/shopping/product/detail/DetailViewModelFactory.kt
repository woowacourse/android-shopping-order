package woowacourse.shopping.product.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCatalogProductRepositoryImpl

class DetailViewModelFactory(
    private val productId: Int,
    private val application: ShoppingApplication,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(
                productId = productId,
                cartProductRepository = RemoteCartProductRepositoryImpl(),
                recentlyViewedProductRepository =
                    RecentlyViewedProductRepositoryImpl(
                        ShoppingDatabase.getInstance(application).recentlyViewedProductDao(),
                        RemoteCatalogProductRepositoryImpl(),
                    ),
                catalogProductRepository = RemoteCatalogProductRepositoryImpl(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
