package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailed: (Throwable) -> Unit,
    )

    fun findProductById(
        id: Int,
        onSuccess: (Product?) -> Unit,
        onFailed: (Throwable) -> Unit,
    )

    fun saveProduct(product: Product)
    fun updateProduct(product: Product)
    fun deleteProduct(product: Product)
}
