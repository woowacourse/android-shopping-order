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

    override fun getAll(): Result<List<Product>> {
        return when (localDataSource.isCached()) {
            true -> return localDataSource.getAll()
            false -> {
                remoteDataSource.getAll()
                    .onSuccess { products ->
                        localDataSource.insertAll(products)
                        return localDataSource.getAll()
                    }.onFailure { throwable ->
                        return Result.failure(throwable)
                    }
            }
        }
    }

    override fun getNext(count: Int): Result<List<Product>> {
        return when (localDataSource.isEndOfCache()) {
            true -> remoteDataSource.getAll().onSuccess { products ->
                localDataSource.insertAll(products)
                return localDataSource.getNext(count)
            }.onFailure { throwable -> return Result.failure(throwable) }
            false -> {
                return localDataSource.getNext(count)
            }
        }
    }

    override fun insert(product: Product): Result<Int> {
        return remoteDataSource.insert(product)
    }

    override fun findById(id: Int): Result<Product> {
        return when (localDataSource.isCached()) {
            true -> localDataSource.findById(id)
            false -> remoteDataSource.findById(id)
        }
    }
}
