package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    fun requestProducts(
        page: Int,
        size: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun requestProductById(
        productId: String,
        onSuccess: (Product?) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun insertProduct(
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun updateProduct(
        productId: String,
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )
}
