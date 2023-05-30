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

    override fun getAll(callback: (Result<List<Product>>) -> Unit) {
        when (localDataSource.isCached()) {
            true -> localDataSource.getAll(callback)
            false -> remoteDataSource.getAll { result ->
                result.onSuccess { products ->
                    localDataSource.insertAll(products)
                    localDataSource.getAll(callback)
                }.onFailure { throwable -> callback(Result.failure(throwable)) }
            }
        }
    }

    override fun getNext(count: Int, callback: (Result<List<Product>>) -> Unit) {
        when (localDataSource.isEndOfCache()) {
            true -> remoteDataSource.getAll { result ->
                result.onSuccess { products ->
                    localDataSource.insertAll(products)
                    localDataSource.getNext(count, callback)
                }.onFailure { throwable -> callback(Result.failure(throwable)) }
            }
            false -> localDataSource.getNext(count, callback)
        }
    }

    override fun insert(product: Product, callback: (Result<Int>) -> Unit) {
        remoteDataSource.insert(product, callback)
    }

    override fun findById(id: Int, callback: (Result<Product>) -> Unit) {
        when (localDataSource.isCached()) {
            true -> localDataSource.findById(id, callback)
            false -> remoteDataSource.findById(id, callback)
        }
    }
}
