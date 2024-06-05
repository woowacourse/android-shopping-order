package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.remote.api.ProductService
import woowacourse.shopping.remote.mapper.toDomain

class ProductRemoteDataSourceImpl(private val productService: ProductService) :
    ProductRemoteDataSource {
    override suspend fun findProductById(id: Long): Product = productService.getProductsById(id = id.toInt()).toDomain()

    override suspend fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Products = productService.getProducts(page = page, size = pageSize).toDomain()
}
