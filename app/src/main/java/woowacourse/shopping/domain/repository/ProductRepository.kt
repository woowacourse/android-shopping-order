package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CatalogProduct
import woowacourse.shopping.domain.model.CatalogProducts

interface ProductRepository {
    fun fetchCatalogProduct(productId: Int): CatalogProduct?

    fun fetchCatalogProducts(productIds: List<Int>): List<CatalogProduct>

    fun fetchProducts(
        page: Int,
        size: Int,
    ): CatalogProducts
}
