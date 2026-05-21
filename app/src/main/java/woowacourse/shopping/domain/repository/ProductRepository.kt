package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.Products

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): Products

    suspend fun getProduct(id: Int): Product?
}
