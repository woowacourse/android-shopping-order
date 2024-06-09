package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ShoppingProductsRepository {
    suspend fun pagedProducts2(page: Int): Result<List<Product>>

    suspend fun allProductsUntilPage2(page: Int): Result<List<Product>>

    suspend fun loadProduct2(id: Long): Result<Product>

    suspend fun isFinalPage2(page: Int): Result<Boolean>
}
