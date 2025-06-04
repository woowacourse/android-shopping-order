package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.response.toProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.ProductApiService
import woowacourse.shopping.domain.model.Product

class ProductRemoteDataSource(
    private val productService: ProductApiService,
) {
    suspend fun getProductById(id: Int): Result<Product?> {
        val response = productService.getProductById(id = id)

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return Result.success(body.toProduct())
            } else {
                Result.success(null)
            }
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    suspend fun getProductsByIds(ids: List<Int>): Result<List<Product>?> {
        val products = mutableListOf<Product>()
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
    ): Result<PagedResult<Product>> {
        val response = productService.getPagedProducts(page = page, size = size)

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val products = body.content.map { it.toProduct() }
                val hasNext = body.last.not()
                Result.success(PagedResult(products, hasNext))
            } else {
                Result.success(PagedResult(emptyList(), false))
            }
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }
}
