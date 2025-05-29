package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage

interface ProductRepository {
    fun loadProduct(
        productId: Long,
        callback: (Result<Product>) -> Unit,
    )

    fun loadSinglePage(
        page: Int,
        pageSize: Int,
        callback: (Result<ProductSinglePage>) -> Unit,
    )
}
