package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getProductById(
        id: Int,
        onResult: (Result<Product?>) -> Unit,
    )

    fun getProductsByIds(
        ids: List<Int>,
        onResult: (Result<List<Product>?>) -> Unit,
    )

    fun getPagedProducts(
        page: Int? = null,
        size: Int? = null,
        onResult: (Result<PagedResult<Product>>) -> Unit,
    )
}
