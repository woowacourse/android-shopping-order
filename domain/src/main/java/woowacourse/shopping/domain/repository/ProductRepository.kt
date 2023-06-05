package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getAllProducts(
        page: Int,
        size: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    )

    fun findProductById(
        id: Int,
        onSuccess: (Product?) -> Unit,
        onFailure: () -> Unit,
    )

    fun insertProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
    fun deleteProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
}
