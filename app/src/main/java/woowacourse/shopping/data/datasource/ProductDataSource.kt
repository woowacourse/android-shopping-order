package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

interface ProductDataSource {
    suspend fun getProductsByOffset(
        page: Int,
        size: Int,
    ): Result<ResponseProductsGetDto, DataError>

    suspend fun getProductsById(id: Long): Result<ResponseProductIdGetDto, DataError>
}
