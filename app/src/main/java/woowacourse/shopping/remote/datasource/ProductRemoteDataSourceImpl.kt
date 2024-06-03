package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.remote.api.ProductService
import woowacourse.shopping.remote.mapper.toDomain

class ProductRemoteDataSourceImpl(private val productService: ProductService) : ProductRemoteDataSource {
    override fun findProductById(id: Long): Result<Product> =
        runCatching {
            productService.getProductsById(id = id.toInt()).execute().body()?.toDomain()
                ?: throw IllegalArgumentException()
        }

    override fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products> =
        runCatching {
            productService.getProducts(page = page, size = pageSize).execute().body()?.toDomain()
                ?: throw IllegalArgumentException()
        }
}
