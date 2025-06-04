package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.ApiCallbackHandler
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

class ProductsDataSource(
    private val service: ProductService,
    private val handler: ApiCallbackHandler,
    private val handler: NetworkResultHandler = NetworkResultHandler(),
) {
    fun singlePage(
        category: String?,
        page: Int?,
        size: Int?,
        callback: (Result<ProductSinglePage>) -> Unit,
    ) = handler.enqueueWithDomainTransform(service.requestProducts(category, page, size), callback)

    suspend fun getProduct(productId: Long): NetworkResult<Product> = handler.execute { service.getProduct(productId).toDomain() }
}
