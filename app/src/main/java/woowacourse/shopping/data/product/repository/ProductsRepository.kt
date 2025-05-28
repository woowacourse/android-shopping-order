package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.product.PageableProducts
import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    fun loadPageableProducts(
        page: Int,
        size: Int,
        onLoad: (Result<PageableProducts>) -> Unit,
    )

    fun getProductById(
        id: Long,
        onLoad: (Result<Product?>) -> Unit,
    )

    fun loadLatestViewedProduct(onLoad: (productId: Result<Product?>) -> Unit)

    fun loadRecentViewedProducts(onLoad: (Result<List<Product>>) -> Unit)

    fun addViewedProduct(
        product: Product,
        onLoad: (Result<Unit>) -> Unit,
    )
}
