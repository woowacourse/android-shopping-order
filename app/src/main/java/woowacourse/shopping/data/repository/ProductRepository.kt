package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.PageResult
import woowacourse.shopping.data.model.Product

interface ProductRepository {
    suspend fun getProductPage(
        page: Int,
        count: Int,
    ): PageResult<Product>

    suspend fun findProduct(id: Long): Product?
}
