package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.data.model.remote.ProductDto
import woowacourse.shopping.data.model.remote.ProductsDto
import woowacourse.shopping.remote.api.ApiService
import woowacourse.shopping.remote.mapper.toData

class ProductDataSourceImpl(private val apiService: ApiService) : ProductDataSource {
    override fun findProductById(id: Long): Result<ProductDto> =
        runCatching {
            apiService.getProductsById(id = id.toInt()).execute().body()?.toData()
                ?: throw IllegalArgumentException()
        }

    override fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<ProductsDto> =
        runCatching {
            apiService.getProducts(page = page, size = pageSize).execute().body()?.toData()
                ?: throw IllegalArgumentException()
        }
}
