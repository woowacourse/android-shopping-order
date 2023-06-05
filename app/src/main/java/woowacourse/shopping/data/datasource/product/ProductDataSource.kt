package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.domain.model.Product

interface ProductDataSource {
    fun requestProducts(
        page: Int,
        size: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    )

    fun requestProductById(
        productId: String,
        onSuccess: (Product?) -> Unit,
        onFailure: () -> Unit,
    )

    fun insertProduct(
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )

    fun updateProduct(
        productId: String,
        product: ProductDto,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )

    fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )
}
