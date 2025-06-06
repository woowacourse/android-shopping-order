package woowacourse.shopping.data.datasource.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponseDto
import woowacourse.shopping.data.service.ProductApiService
import woowacourse.shopping.data.util.requireBody

class ProductRemoteDataSource(
    private val productService: ProductApiService,
) {
    suspend fun getProductById(id: Int): Result<ProductDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                productService.getProductById(id = id).requireBody()
            }
        }

    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<ProductResponseDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                productService.getPagedProducts(page = page, size = size).requireBody()
            }
        }
}
