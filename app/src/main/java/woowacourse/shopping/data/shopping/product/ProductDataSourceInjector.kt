package woowacourse.shopping.data.shopping.product

import woowacourse.shopping.remote.service.ProductService

object ProductDataSourceInjector {
    @Volatile
    private var instance: ProductDataSource? = null

    fun productDataSource(): ProductDataSource =
        instance ?: synchronized(this) {
            instance ?: DefaultProductDataSource(ProductService.instance()).also {
                instance = it
            }
        }
}
