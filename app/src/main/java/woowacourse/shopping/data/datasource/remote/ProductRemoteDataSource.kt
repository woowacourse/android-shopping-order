package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.ProductApiService

class ProductRemoteDataSource(
    private val productService: ProductApiService,
) {
    suspend fun getProductById(id: Int): Result<ProductDto?> {
        return productService.getProductById(id = id)
    }

    suspend fun getProductsByIds(ids: List<Int>): Result<List<ProductDto>?> {
        val products = mutableListOf<ProductDto>()
        val exceptions = mutableListOf<Throwable>()

        ids.forEach { id ->
            val result = getProductById(id)
            result
                .onSuccess { product ->
                    product?.let { products.add(it) }
                }.onFailure { throwable ->
                    exceptions.add(throwable)
                }
        }
        return if (exceptions.isNotEmpty()) {
            Result.failure(exceptions.first())
        } else {
            Result.success(products)
        }
    }

    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<ProductDto>> {
        val result = productService.getPagedProducts(page = page, size = size)

        return result.mapCatching { dto ->
            val products = dto.content
            val hasNext = !dto.last
            PagedResult(products, hasNext)
        }
    }
}
