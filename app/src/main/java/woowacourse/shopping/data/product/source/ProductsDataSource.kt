package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.dto.ProductsResponse
import woowacourse.shopping.data.product.entity.ProductEntity

interface ProductsDataSource {
    fun pagedProducts(
        page: Int,
        size: Int,
    ): ProductsResponse?

    fun getProductById(id: Long): ProductEntity?

    fun getProductsByCategory(category: String): List<ProductEntity>?
}
