package woowacourse.shopping.data.shopping.product.datasource

import woowacourse.shopping.data.common.ioExecutor
import woowacourse.shopping.remote.service.ProductService

object ProductDataSourceInjector {
    @Volatile
    private var instance: ProductDataSource? = null

    fun productDataSource(): ProductDataSource =
        instance ?: synchronized(this) {
            instance ?: DefaultProductDataSource(
                ioExecutor,
                ProductService.instance()
            ).also {
                instance = it
            }
        }
}
