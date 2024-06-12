package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.product.RetrofitProductDataSource
import woowacourse.shopping.data.remote.repository.ProductRepositoryImpl
import woowacourse.shopping.domain.repository.ProductRepository

object ProductRepositoryInjector {
    var instance: ProductRepository =
        ProductRepositoryImpl(
            RetrofitProductDataSource(),
        )
        private set

    fun setInstance(productRepository: ProductRepository) {
        instance = productRepository
    }
}
