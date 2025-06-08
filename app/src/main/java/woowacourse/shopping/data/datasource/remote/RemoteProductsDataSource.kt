package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

class RemoteProductsDataSource(
    private val service: ProductService,
    private val handler: NetworkResultHandler,
) {
    suspend fun singlePage(
        category: String?,
        page: Int?,
        size: Int?,
    ): Result<ProductSinglePage> =
        handler.handleResult {
            service.singlePage(category, page, size).toDomain()
        }

    suspend fun getProduct(productId: Long): Result<Product> =
        handler.handleResult {
            service.getProduct(productId).toDomain()
        }
}
