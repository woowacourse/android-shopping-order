package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<Product>>

    fun getProductIsLast(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<Boolean>

    fun getProductById(id: Int): Result<Product>
}
