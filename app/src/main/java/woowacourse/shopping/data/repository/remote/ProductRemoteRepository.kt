package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.mapper.toDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRemoteRepository(
    private val productDataSource: ProductDataSource,
) : ProductRepository {

    override fun getAllProducts(
        page: Page,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productDataSource.requestProducts(
            page = page.value,
            size = page.sizePerPage,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun findProductById(
        id: Int,
        onSuccess: (Product?) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productDataSource.requestProductById(
            id.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun insertProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productDataSource.insertProduct(
            product.toDto(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productDataSource.updateProduct(
            product.id.toString(),
            product.toDto(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun deleteProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        productDataSource.deleteProduct(
            product.id.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
