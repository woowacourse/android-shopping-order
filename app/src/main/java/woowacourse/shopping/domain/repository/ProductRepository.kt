package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

interface ProductRepository {
    suspend fun loadProduct(productId: Long): NetworkResult<Product>

    fun loadSinglePage(
        category: String? = null,
        page: Int?,
        pageSize: Int?,
        callback: (Result<ProductSinglePage>) -> Unit,
    )
}
