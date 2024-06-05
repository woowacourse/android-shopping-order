package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.data.service.ProductService

class ProductRemoteDataSourceImpl(private val service: ProductService) : ProductRemoteDataSource {
    override suspend fun getProductsByOffset(
        page: Int,
        size: Int,
    ): Result<ResponseProductsGetDto> =
        runCatching {
            service.getProductsByOffset(page = page, size = size)
        }

    override suspend fun getProductsByCategory(
        category: String,
        page: Int,
    ): Result<ResponseProductsGetDto> =
        runCatching {
            service.getProductsByCategory(category = category, page = page)
        }

    override suspend fun getProductsById(id: Long): Result<ResponseProductIdGetDto> =
        runCatching {
            service.getProductsById(id = id)
        }
}
