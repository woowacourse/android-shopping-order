package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto

interface ProductDataSource {
    fun getProductsByOffset(
        page: Int,
        size: Int,
    ): ResponseProductsGetDto?

    fun getProductsById(id: Long): ResponseProductIdGetDto?
}
