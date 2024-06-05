package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.ProductItemDomain

interface ProductRepository {
    fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
        onSuccess: (ProductDomain) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun getProductById(
        id: Int,
        onSuccess: (ProductItemDomain) -> Unit,
        onFailure: (Throwable) -> Unit,
    )
}
