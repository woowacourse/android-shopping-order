package woowacourse.shopping.domain.repository.product

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun loadProducts(page: Int): ResponseResult<List<Product>>

    suspend fun loadProduct(id: Long): ResponseResult<Product>

    suspend fun isFinalPage(page: Int): ResponseResult<Boolean>
}
