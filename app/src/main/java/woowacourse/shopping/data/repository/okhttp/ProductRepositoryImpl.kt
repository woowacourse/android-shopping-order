package woowacourse.shopping.data.repository.okhttp

import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.okhttp.product.ProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteProductDataSource: ProductService,
) : ProductRepository {

    override fun getAllProducts(): List<Product> {
        return remoteProductDataSource.getAllProduct().map { it.toDomain() }
    }

    override fun findProductById(id: Int): Product? =
        remoteProductDataSource.findProductById(id)?.toDomain()

    override fun insertProduct(product: Product) {
        remoteProductDataSource.insertProduct(product.toData())
    }

    override fun updateProduct(product: Product) {
        remoteProductDataSource.updateProduct(product.toData())
    }

    override fun deleteProduct(product: Product) {
        remoteProductDataSource.deleteProduct(product.toData())
    }
}
