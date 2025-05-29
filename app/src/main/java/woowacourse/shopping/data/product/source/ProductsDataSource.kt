package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.PageableProductData
import woowacourse.shopping.data.product.entity.ProductEntity

interface ProductsDataSource {
    fun pageableProducts(
        page: Int,
        size: Int,
    ): PageableProductData

    fun getProductById(id: Long): ProductEntity?

    fun getProductsByCategory(category: String): List<ProductEntity>?
}
