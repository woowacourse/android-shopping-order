package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Product

interface ProductRepository {
    suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductResponseResult

    suspend fun getProductById(id: String): Product
}
