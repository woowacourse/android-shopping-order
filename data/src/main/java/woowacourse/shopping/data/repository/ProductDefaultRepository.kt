package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.ProductLocalDataSource
import woowacourse.shopping.data.remote.ProductRemoteDataSource
import woowacourse.shopping.model.Product

class ProductDefaultRepository(
    private val localDataSource: ProductLocalDataSource,
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {

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

    override fun findById(id: Int): Result<Product> {
        return when (localDataSource.isCached()) {
            true -> localDataSource.findById(id)
            false -> remoteDataSource.findById(id)
        }
    }
}
