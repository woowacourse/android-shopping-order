package woowacourse.shopping.data.shopping.product.datasource

import woowacourse.shopping.data.shopping.product.ProductPageData
import woowacourse.shopping.domain.entity.Product

interface ProductDataSource {
    suspend fun products(
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData>

    suspend fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData>

    suspend fun productById(id: Long): Result<Product>
}
