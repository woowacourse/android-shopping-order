package woowacourse.shopping.data.product

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductResponse

interface ProductDataSource {
    fun loadByPaged(page: Int): ResponseResult<ProductResponse>

    fun loadById(id: Long): ResponseResult<ProductDto>

    fun loadByCategory(category: String): ResponseResult<ProductResponse>

    fun shutDown(): Boolean
}
