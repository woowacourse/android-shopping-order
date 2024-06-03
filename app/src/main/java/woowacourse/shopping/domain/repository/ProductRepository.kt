package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun loadPagingProducts(offset: Int): Result<List<Product>>

    fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>>

    fun getProduct(productId: Long): Result<Product>
}
