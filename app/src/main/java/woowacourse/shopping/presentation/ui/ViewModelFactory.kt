package woowacourse.shopping.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.local.LocalCartDataSourceImpl
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.local.AppDatabase
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recent.RecentProductRepositoryImpl
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import woowacourse.shopping.presentation.ui.detail.ProductDetailViewModel
import woowacourse.shopping.presentation.ui.shopping.ShoppingViewModel

class ViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                val cartDao = AppDatabase.instanceOrNull.cartDao()
                ProductDetailViewModel(
                    ProductRepositoryImpl(),
                    CartRepositoryImpl(
                        localCartDataSource = LocalCartDataSourceImpl(cartDao),
                        remoteCartDataSource = RemoteCartDataSource(),
                    ),
                    RecentProductRepositoryImpl(recentDao),
                ) as T
            }

            modelClass.isAssignableFrom(ShoppingViewModel::class.java) -> {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                val cartDao = AppDatabase.instanceOrNull.cartDao()
                ShoppingViewModel(
                    productRepository = ProductRepositoryImpl(),
                    recentRepository = RecentProductRepositoryImpl(recentDao),
                    cartRepository =
                        CartRepositoryImpl(
                            localCartDataSource = LocalCartDataSourceImpl(cartDao),
                            remoteCartDataSource = RemoteCartDataSource(),
                        ),
                ) as T
            }

            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                val cartDao = AppDatabase.instanceOrNull.cartDao()
                CartViewModel(
                    cartRepository =
                        CartRepositoryImpl(
                            localCartDataSource = LocalCartDataSourceImpl(cartDao),
                            remoteCartDataSource = RemoteCartDataSource(),
                        ),
                ) as T
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }
}
