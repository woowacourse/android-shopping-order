package woowacourse.shopping.data.repository.okhttp

import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.okhttp.product.ProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteProductDataSource: ProductService,
) : ProductRepository {

    override fun getAllProducts(
        page: Page,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.getAllProduct().map { it.toDomain() }
    }

    override fun findProductById(
        id: Int,
        onSuccess: (Product?) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.findProductById(id)?.toDomain()
    }

    override fun insertProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.findProductById(product.id)?.toDomain()
    }

    override fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.updateProduct(product.toData())
    }

    override fun deleteProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.deleteProduct(product.toData())
    }
}
