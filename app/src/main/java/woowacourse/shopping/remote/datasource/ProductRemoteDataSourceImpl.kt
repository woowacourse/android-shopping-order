package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.model.remote.ProductDto
import woowacourse.shopping.data.model.remote.ProductsDto
import woowacourse.shopping.remote.api.ProductService
import woowacourse.shopping.remote.mapper.toData

class ProductRemoteDataSourceImpl(private val productService: ProductService) : ProductRemoteDataSource {
    override fun findProductById(id: Long): Result<ProductDto> =
        runCatching {
            productService.getProductsById(id = id.toInt()).execute().body()?.toData()
                ?: throw IllegalArgumentException()
        }

    override fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<ProductsDto> =
        runCatching {
            productService.getProducts(page = page, size = pageSize).execute().body()?.toData()
                ?: throw IllegalArgumentException()
        }
}
