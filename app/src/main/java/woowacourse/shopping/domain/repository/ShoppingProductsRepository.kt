package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ShoppingProductsRepository {
    suspend fun pagedProducts(page: Int): Result<List<Product>>

    suspend fun allProductsUntilPage(page: Int): Result<List<Product>>

    suspend fun loadProduct(id: Long): Result<Product>

    suspend fun isFinalPage(page: Int): Result<Boolean>
}
