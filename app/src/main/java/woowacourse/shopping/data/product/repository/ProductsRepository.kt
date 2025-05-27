package woowacourse.shopping.data.product.repository

import woowacourse.shopping.domain.product.Product

interface ProductsRepository {
    fun load(page: Int, size: Int, onLoad: (Result<List<Product>>) -> Unit)

    fun loadLatestViewedProduct(onLoad: (Result<Product?>) -> Unit)

    fun loadLastViewedProducts(onLoad: (Result<List<Product>>) -> Unit)

    fun recordViewedProduct(product: Product)
}
