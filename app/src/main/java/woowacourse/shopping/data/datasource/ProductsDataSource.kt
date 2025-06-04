package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

class ProductsDataSource(
    private val service: ProductService,
    private val handler: NetworkResultHandler,
) {
    suspend fun singlePage(
        category: String?,
        page: Int?,
        size: Int?,
    ): NetworkResult<ProductSinglePage> =
        handler.execute {
            service.singlePage(category, page, size).toDomain()
        }

    suspend fun getProduct(productId: Long): NetworkResult<Product> = handler.execute { service.getProduct(productId).toDomain() }
}
