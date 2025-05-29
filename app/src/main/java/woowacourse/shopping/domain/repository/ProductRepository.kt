package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products

interface ProductRepository {
    fun fetchCatalogProduct(
        productId: Long,
        callback: (Result<Product?>) -> Unit,
    )

    fun fetchCatalogProducts(
        productIds: List<Int>,
        callback: (Result<List<Product>>) -> Unit,
    )

    fun fetchProducts(
        page: Int,
        size: Int,
        category: String? = null,
        callback: (Result<Products>) -> Unit,
    )

    fun fetchAllProducts(callback: (Result<List<Product>>) -> Unit)
}
