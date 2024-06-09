package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.product.DefaultProductDataSource
import woowacourse.shopping.data.remote.repository.ProductRepositoryImpl
import woowacourse.shopping.domain.ProductRepository

object ProductRepositoryInjector {
    var instance: ProductRepository =
        ProductRepositoryImpl(
            DefaultProductDataSource(),
        )
        private set

    fun setInstance(productRepository: ProductRepository) {
        instance = productRepository
    }
}
