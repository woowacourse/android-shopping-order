package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.product.Category
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Products

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): Products

    suspend fun getCategoryProducts(
        category: Category,
        page: Int,
        pageSize: Int,
    ): Products

    suspend fun getProduct(id: Int): Product?
}
