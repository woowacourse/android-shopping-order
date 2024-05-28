package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Product

interface ProductRepository {
    fun load(
        startPage: Int,
        pageSize: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    )

    fun loadById(
        id: Long,
        onSuccess: (Product) -> Unit,
        onFailure: () -> Unit,
    )
}
