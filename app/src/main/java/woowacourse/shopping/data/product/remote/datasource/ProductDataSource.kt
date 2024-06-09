package woowacourse.shopping.data.product.remote.datasource

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.product.remote.dto.ProductDto
import woowacourse.shopping.data.product.remote.dto.ProductResponse

interface ProductDataSource {
    suspend fun loadByPaged(page: Int): ResponseResult<ProductResponse>

    suspend fun loadById(id: Long): ResponseResult<ProductDto>

    suspend fun loadByCategory(category: String): ResponseResult<ProductResponse>

    fun shutDown(): Boolean
}
