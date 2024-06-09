package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ShoppingProductsRepository {
    fun pagedProducts(page: Int): List<Product>

    fun allProductsUntilPage(page: Int): List<Product>

    fun loadProduct(id: Long): Product

    fun isFinalPage(page: Int): Boolean

    suspend fun pagedProducts2(page: Int): Result<List<Product>>

    suspend fun allProductsUntilPage2(page: Int): Result<List<Product>>

    suspend fun loadProduct2(id: Long): Result<Product>

    suspend fun isFinalPage2(page: Int): Result<Boolean>
}
