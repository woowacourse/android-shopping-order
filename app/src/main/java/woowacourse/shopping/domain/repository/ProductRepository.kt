package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getProductById(
        id: Int,
        onSuccess: (Product?) -> Unit,
    )

    fun getProductsByIds(
        ids: List<Int>,
        onSuccess: (List<Product>?) -> Unit,
    )

    fun getPagedProducts(
        page: Int? = null,
        size: Int? = null,
        onSuccess: (PagedResult<Product>) -> Unit,
    )
}
