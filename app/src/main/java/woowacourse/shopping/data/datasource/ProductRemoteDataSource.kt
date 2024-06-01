package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto

interface ProductRemoteDataSource {
    fun getProductsByOffset(
        page: Int,
        size: Int,
    ): Result<ResponseProductsGetDto>

    fun getProductsByCategory(
        category: String,
        page: Int,
    ): Result<ResponseProductsGetDto>

    fun getProductsById(id: Long): Result<ResponseProductIdGetDto>
}
