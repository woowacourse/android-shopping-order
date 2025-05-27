package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    fun load(
        page: Int,
        size: Int,
        onResult: (Result<List<Product>>) -> Unit,
    )

    fun getRecentWatchingProducts(
        size: Int,
        onResult: (Result<List<Product>>) -> Unit,
    )

    fun updateRecentWatchingProduct(
        product: Product,
        onResult: (Result<Unit>) -> Unit,
    )

    fun getProduct(
        productId: Long,
        onResult: (Result<Product?>) -> Unit,
    )
}
