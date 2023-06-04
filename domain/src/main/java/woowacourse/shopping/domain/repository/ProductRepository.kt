package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.page.Page

interface ProductRepository {
    fun getProductsByPage(
        page: Page,
        onSuccess: (List<Product>) -> Unit,
        onFailed: (Throwable) -> Unit,
    )

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
