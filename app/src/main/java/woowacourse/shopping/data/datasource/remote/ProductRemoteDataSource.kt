package woowacourse.shopping.data.datasource.remote

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.dto.response.toProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.ProductApiService
import woowacourse.shopping.data.util.requireBody
import woowacourse.shopping.domain.model.Product

class ProductRemoteDataSource(
    private val productService: ProductApiService,
) {
    suspend fun getProductById(id: Int): Result<Product> =
        runCatching {
            val responseBody = productService.getProductById(id = id).requireBody()
            responseBody.toProduct()
        }

    suspend fun getProductsByIds(ids: List<Int>): Result<List<Product>> =
        runCatching {
            coroutineScope {
                ids.map { async { getProductById(it).getOrThrow() } }.awaitAll()
            }
        }

    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<Product>> =
        runCatching {
            val responseBody =
                productService.getPagedProducts(page = page, size = size).requireBody()
            val products = responseBody.content.map { it.toProduct() }
            val hasNext = responseBody.last.not()
            PagedResult(products, hasNext)
        }
}
