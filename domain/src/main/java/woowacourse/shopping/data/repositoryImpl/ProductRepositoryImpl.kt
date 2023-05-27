package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.localDataSource.ProductLocalDataSource
import woowacourse.shopping.data.remoteDataSource.ProductRemoteDataSource
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.model.Product

class ProductRepositoryImpl(
    private val localDataSource: ProductLocalDataSource,
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {
    override fun clearCache() {
        localDataSource.clear()
    }

    override fun getAll(callback: (List<Product>?) -> Unit) {
        when (localDataSource.isCached()) {
            true -> localDataSource.getAll(callback)
            false -> remoteDataSource.getAll {
                localDataSource.insertAll(it)
                localDataSource.getAll(callback)
            }
        }
    }

    override fun getNext(count: Int, callback: (List<Product>?) -> Unit) {
        when (localDataSource.isEndOfCache()) {
            true -> remoteDataSource.getAll { products ->
                localDataSource.insertAll(products)
                localDataSource.getNext(count, callback)
            }
            false -> localDataSource.getNext(count, callback)
        }
    }

    override fun insert(product: Product, callback: (Int) -> Unit) {
        remoteDataSource.insert(product, callback)
    }

    override fun findById(id: Int, callback: (Product?) -> Unit) {
        when (localDataSource.isCached()) {
            true -> localDataSource.findById(id, callback)
            false -> remoteDataSource.findById(id, callback)
        }
    }
}
