package woowacourse.shopping.repository

import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Products

interface ProductRepository {
    val size: Int

    suspend fun getProducts(
        fromIndex: Int,
        limit: Int,
    ): Result<Products>

    suspend fun getProductsByCategory(
        category: String,
        limit: Int,
    ): Result<Products>

    suspend fun hasNext(current: Int): Result<Boolean>

    suspend fun findAllByIds(ids: Set<Long>): Result<Map<Long, Product>>
}
