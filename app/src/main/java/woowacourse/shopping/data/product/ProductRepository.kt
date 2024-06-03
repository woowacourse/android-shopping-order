package woowacourse.shopping.data.product

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>>

    fun find(id: Long): Result<Product>

    fun getProductsByCategory(category: String): Result<List<Product>>
}
