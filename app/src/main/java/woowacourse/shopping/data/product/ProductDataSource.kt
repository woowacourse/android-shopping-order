package woowacourse.shopping.data.product

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductResponse

interface ProductDataSource {
    suspend fun loadByPaged(page: Int): ResponseResult<ProductResponse>

    suspend fun loadById(id: Long): ResponseResult<ProductDto>

    suspend fun loadByCategory(category: String): ResponseResult<ProductResponse>

    fun shutDown(): Boolean
}
