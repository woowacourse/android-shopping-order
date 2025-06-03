package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.ApiCallbackHandler
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

class ProductsDataSource(
    private val service: ProductService,
    private val handler: ApiCallbackHandler,
) {
    fun singlePage(
        category: String?,
        page: Int?,
        size: Int?,
        callback: (Result<ProductSinglePage>) -> Unit,
    ) = handler.enqueueWithDomainTransform(service.requestProducts(category, page, size), callback)

    fun getProduct(
        productId: Long,
        callback: (Result<Product>) -> Unit,
    ) = handler.enqueueWithDomainTransform(service.getProduct(productId), callback)
}
