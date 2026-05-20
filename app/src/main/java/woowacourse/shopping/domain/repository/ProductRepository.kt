package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Products

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): Products

    suspend fun getCategoryProducts(
        category: String,
        page: Int,
        pageSize: Int,
    ):Products

    suspend fun getProduct(id: Int): Product?
}
