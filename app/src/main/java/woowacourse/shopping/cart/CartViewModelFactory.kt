package woowacourse.shopping.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.di.RepositoryProvider

class CartViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                cartProductRepository = RepositoryProvider.provideCartProductRepository(),
                catalogProductRepository = RepositoryProvider.provideCatalogProductRepository(),
                recentlyViewedProductRepository = RepositoryProvider.provideRecentlyViewedProductRepository(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
