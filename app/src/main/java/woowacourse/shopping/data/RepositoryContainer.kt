package woowacourse.shopping.data

import android.content.Context
import woowacourse.shopping.data.repository.DefaultCartItemRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.DefaultRecentlyViewedProductRepository
import woowacourse.shopping.data.repository.DefaultUserRepository

class RepositoryContainer(context: Context) {
    private val localDataSourceContainer = LocalDataSourceContainer(context)
    private val remoteDataSourceContainer = RemoteDataSourceContainer()

    val productRepository by lazy { DefaultProductRepository(remoteDataSourceContainer.product) }
    val recentlyViewedProductRepository by lazy {
        DefaultRecentlyViewedProductRepository(
            localDataSourceContainer.recentlyViewedProduct,
            remoteDataSourceContainer.product
        )
    }
    val cartItemRepository by lazy { DefaultCartItemRepository(remoteDataSourceContainer.cart) }
    val userRepository by lazy {
        DefaultUserRepository(
            localDataSourceContainer.user,
            remoteDataSourceContainer.user
        )
    }
    val orderRepository by lazy { DefaultOrderRepository(remoteDataSourceContainer.order) }
}
