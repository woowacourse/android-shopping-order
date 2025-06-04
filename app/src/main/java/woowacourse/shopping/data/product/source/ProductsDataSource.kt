package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.PagedProductsData
import woowacourse.shopping.data.product.entity.ProductEntity

interface ProductsDataSource {
    fun pagedProducts(
        page: Int,
        size: Int,
    ): PagedProductsData

    fun getProductById(id: Long): ProductEntity?

    fun getProductsByCategory(category: String): List<ProductEntity>?
}
