package woowacourse.shopping.data.shopping.product.datasource

import woowacourse.shopping.data.shopping.product.ProductPageData
import woowacourse.shopping.domain.entity.Product

interface ProductDataSource {
    fun products(
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData>

    fun products(category: String, currentPage: Int, size: Int): Result<ProductPageData>
    fun productById(id: Long): Result<Product>
}
