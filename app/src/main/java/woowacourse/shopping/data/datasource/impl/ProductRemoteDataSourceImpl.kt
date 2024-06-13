package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.data.exception.ShoppingError
import woowacourse.shopping.data.remote.service.ProductService

class ProductRemoteDataSourceImpl(private val service: ProductService) : ProductRemoteDataSource {
    override suspend fun getProductsByOffset(
        page: Int,
        size: Int,
    ): Result<ResponseProductsGetDto> =
        runCatching {
            service.getProductsByOffset(page = page, size = size).body()
                ?: throw ShoppingError.ProductNotFound
        }

    override suspend fun getProductsByCategory(
        category: String,
        page: Int,
    ): Result<ResponseProductsGetDto> =
        runCatching {
            service.getProductsByCategory(category = category, page = page).body()
                ?: throw ShoppingError.ProductNotFound
        }

    override suspend fun getProductsById(id: Long): Result<ResponseProductIdGetDto> =
        runCatching {
            service.getProductsById(id = id).body()
                ?: throw ShoppingError.ProductNotFoundWithId(id)
        }
}
