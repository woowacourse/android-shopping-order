package woowacourse.shopping.data.product

import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductResponse

interface ProductDataSource {
    fun findByPaged(page: Int): ResponseResult<ProductResponse>

    fun findById(id: Long): ResponseResult<ProductDto>

    fun findByCategory(category: String): ResponseResult<ProductResponse>

    fun shutDown(): Boolean
}
