package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto

interface ApiHandleProductDataSource {
    suspend fun getProductsByOffset(
        page: Int,
        size: Int,
    ): ApiResult<ResponseProductsGetDto>

    suspend fun getProductsById(id: Long): ApiResult<ResponseProductIdGetDto>
}
