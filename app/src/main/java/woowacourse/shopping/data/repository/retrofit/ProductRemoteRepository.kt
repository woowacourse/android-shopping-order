package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.mapper.toDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRemoteRepository(
    private val productDataSource: ProductRemoteDataSource,
) : ProductRepository {

    override fun getAllProducts(
        page: Int,
        size: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        productDataSource.requestProducts(
            page = page,
            size = size,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun findProductById(
        id: Int,
        onSuccess: (Product?) -> Unit,
        onFailure: () -> Unit,
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
        onFailure: () -> Unit,
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
        onFailure: () -> Unit,
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
        onFailure: () -> Unit,
    ) {
        productDataSource.deleteProduct(
            product.id.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
