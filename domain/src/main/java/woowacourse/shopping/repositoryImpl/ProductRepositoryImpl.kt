package woowacourse.shopping.repositoryImpl

import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl(
    private val localDatabase: ProductRepository,
    private val remoteDatabase: ProductRepository
) : ProductRepository {
    override fun getAll(callback: (List<Product>?) -> Unit) {
        remoteDatabase.getAll(callback)
    }

    override fun getNext(count: Int, callback: (List<Product>?) -> Unit) {
        remoteDatabase.getNext(count, callback)
    }

    override fun insert(product: Product, callback: (Int) -> Unit) {
        remoteDatabase.insert(product, callback)
    }

    override fun findById(id: Int, callback: (Product?) -> Unit) {
        remoteDatabase.findById(id, callback)
    }
}
