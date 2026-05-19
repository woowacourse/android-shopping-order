package woowacourse.shopping.domain.repository

import kotlinx.collections.immutable.ImmutableList
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(
        offset: Int,
        limit: Int,
        category: String? = null,
    ): ImmutableList<Product>

    suspend fun getProductById(id: Long): Product
}
