package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.domain.model.Product

interface ProductRepositoryNew {
    fun getAllProducts(
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
