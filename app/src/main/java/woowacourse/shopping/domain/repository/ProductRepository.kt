package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

interface ProductRepository {
    suspend fun loadProduct(productId: Long): Result<Product>

    suspend fun loadSinglePage(
        category: String? = null,
        page: Int?,
        pageSize: Int?,
    ): Result<ProductSinglePage>
}
