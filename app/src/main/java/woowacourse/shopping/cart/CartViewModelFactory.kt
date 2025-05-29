package woowacourse.shopping.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCatalogProductRepositoryImpl

class CartViewModelFactory(
    private val application: ShoppingApplication,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                cartProductRepository = RemoteCartProductRepositoryImpl(),
                catalogProductRepository = RemoteCatalogProductRepositoryImpl(),
                RecentlyViewedProductRepositoryImpl(
                    ShoppingDatabase.getInstance(application).recentlyViewedProductDao(),
                    RemoteCatalogProductRepositoryImpl(),
                ),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
