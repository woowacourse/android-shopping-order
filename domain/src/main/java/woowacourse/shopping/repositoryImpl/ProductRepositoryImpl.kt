package woowacourse.shopping.repositoryImpl

import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl(
    private val localDatabase: ProductRepository,
    private val remoteDatabase: ProductRepository
) : ProductRepository {
    private var isInitialized = false

    override fun getAll(): List<Product> {
        remoteDatabase.getAll().forEach {
            localDatabase.insert(it)
        }
        return localDatabase.getAll()
    }

    override fun getNext(count: Int): List<Product> {
        if (!isInitialized) {
            getAll()
            isInitialized = true
        }
        return localDatabase.getNext(count)
    }

    override fun insert(product: Product): Int {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int): Product {
        return localDatabase.findById(id)
    }
}
