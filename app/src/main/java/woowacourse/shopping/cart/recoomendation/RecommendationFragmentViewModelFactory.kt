package woowacourse.shopping.cart.recoomendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.repository.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCatalogProductRepositoryImpl

class RecommendationFragmentViewModelFactory(
    private val application: ShoppingApplication,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendationFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendationFragmentViewModel(
                cartProductRepository = RemoteCartProductRepositoryImpl(),
                catalogProductRepository = RemoteCatalogProductRepositoryImpl(),
                recentlyViewedProductRepository = RecentlyViewedProductRepositoryImpl(
                    ShoppingDatabase.getInstance(application).recentlyViewedProductDao(),
                    RemoteCatalogProductRepositoryImpl(),
                ),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
