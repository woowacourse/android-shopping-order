package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.product.PageableProducts
import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    fun load(
        page: Int,
        size: Int,
        onLoad: (Result<PageableProducts>) -> Unit,
    )

    fun loadProductById(
        id: Long,
        onLoad: (Result<Product?>) -> Unit,
    )

    fun loadLatestViewedProduct(onLoad: (Result<Product?>) -> Unit)

    fun loadLastViewedProducts(onLoad: (Result<List<Product>>) -> Unit)

    fun recordViewedProduct(
        product: Product,
        onLoad: (Result<Unit>) -> Unit,
    )
}
