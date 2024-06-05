package woowacourse.shopping.data.shopping

import android.content.Context
import androidx.annotation.VisibleForTesting
import woowacourse.shopping.data.shopping.product.datasource.ProductDataSourceInjector
import woowacourse.shopping.data.shopping.recent.RecentProductDataSourceInjector
import woowacourse.shopping.domain.repository.ProductRepository

object ProductRepositoryInjector {
    @Volatile
    private var instance: ProductRepository? = null

    fun productRepository(context: Context): ProductRepository =
        instance ?: synchronized(this) {
            instance ?: DefaultProductRepository(
                ProductDataSourceInjector.productDataSource(),
                RecentProductDataSourceInjector.recentProductDataSource(context),
            ).also { instance = it }
        }

    @VisibleForTesting
    fun setShoppingRepository(productRepository: ProductRepository) {
        instance = productRepository
    }

    @VisibleForTesting
    fun clear() {
        instance = null
    }
}
