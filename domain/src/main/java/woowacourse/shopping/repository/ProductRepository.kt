package woowacourse.shopping.repository

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getAll(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun findById(
        id: Int,
        onSuccess: (Product) -> Unit,
        onFailure: (Exception) -> Unit,
    )
}
