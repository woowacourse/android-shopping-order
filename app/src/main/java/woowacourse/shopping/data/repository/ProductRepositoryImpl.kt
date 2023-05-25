package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.page.Page
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
        remoteProductDataSource.addProduct(product.toData())
    }

    override fun updateProduct(product: Product) {
        remoteProductDataSource.adjustProduct(product.toData())
    }

    override fun deleteProduct(product: Product) {
        remoteProductDataSource.deleteProduct(product.toData())
    }
}
