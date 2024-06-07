package woowacourse.shopping.domain.repository.product

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    suspend fun loadProducts(page: Int): List<Product>

    suspend fun loadProduct(id: Long): Product

    suspend fun isFinalPage(page: Int): Boolean
}
