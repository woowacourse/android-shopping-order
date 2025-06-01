package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.enqueueWithTransform
import woowacourse.shopping.data.network.service.ProductService
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

class ProductsDataSource(private val service: ProductService) {
    fun singlePage(
        category: String?,
        page: Int?,
        size: Int?,
        callback: (Result<ProductSinglePage?>) -> Unit,
    ) = service.requestProducts(category, page = page, size = size).enqueueWithTransform(
        transform = { it.body()?.toDomain() },
        callback = callback,
    )

    fun getProduct(
        productId: Long,
        callback: (Result<Product?>) -> Unit,
    ) = service.getProduct(productId).enqueueWithTransform(
        transform = { it.body()?.toDomain() },
        callback = callback,
    )
}
