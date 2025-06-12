package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.PageableProductData
import woowacourse.shopping.data.product.entity.ProductEntity

interface ProductsDataSource {
    suspend fun products(category: String): List<ProductEntity>

    suspend fun pageableProducts(
        page: Int,
        size: Int,
    ): PageableProductData

    suspend fun getProductById(id: Long): ProductEntity?
}
