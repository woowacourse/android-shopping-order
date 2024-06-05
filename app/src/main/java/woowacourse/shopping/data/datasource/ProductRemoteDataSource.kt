package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto

interface ProductRemoteDataSource {
    suspend fun getProductsByOffset(
        page: Int,
        size: Int,
    ): Result<ResponseProductsGetDto>

    suspend fun getProductsByCategory(
        category: String,
        page: Int,
    ): Result<ResponseProductsGetDto>

    suspend fun getProductsById(id: Long): Result<ResponseProductIdGetDto>
}
