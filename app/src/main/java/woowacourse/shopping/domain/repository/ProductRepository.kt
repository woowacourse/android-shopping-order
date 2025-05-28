package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun fetchProduct(
        id: Long,
        onResult: (Result<Product>) -> Unit,
    )

    fun fetchProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<Product>>) -> Unit,
    )
}
