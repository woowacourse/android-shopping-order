package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.page.Page

interface ProductRepository {
    fun getAllProducts(
        page: Page,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun findProductById(
        id: Int,
        onSuccess: (Product?) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun insertProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )
    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )
    fun deleteProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )
}