package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.localDataSource.CartLocalDataSource
import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.dto.toDomain
import woowacourse.shopping.model.CartProduct

class CartRepositoryImpl(
    private val localDataSource: CartLocalDataSource,
    private val remoteDataSource: CartRemoteDataSource
) : CartRepository {

    override fun getAll(): Result<List<CartProduct>> {
        remoteDataSource.getAll()
            .onSuccess {
                localDataSource.replaceAll(it.toDomain())
                return localDataSource.getAll()
            }
        return Result.failure(Throwable("Failed to get all"))
    }

    override fun getPage(offset: Int, size: Int): Result<List<CartProduct>> {
        return localDataSource.getPage(offset, size)
    }

    override fun getCurrentPage(): Int {
        return localDataSource.getCurrentPage()
    }

    override fun getCurrentPageChecked(): Int {
        return localDataSource.getCurrentPageChecked()
    }

    override fun getChecked(): Result<List<CartProduct>> {
        return localDataSource.getChecked()
    }

    override fun getTotalQuantity(): Int {
        return localDataSource.getTotalQuantity()
    }

    override fun getTotalCheckedQuantity(): Int {
        return localDataSource.getTotalCheckedQuantity()
    }

    override fun getTotalCheckedPrice(): Int {
        return localDataSource.getTotalCheckedPrice()
    }

    override fun hasNextPage(): Boolean {
        return localDataSource.hasNextPage()
    }

    override fun hasPrevPage(): Boolean {
        return localDataSource.hasPrevPage()
    }

    override fun updateCountWithProductId(productId: Int, count: Int): Result<Int> {
        localDataSource.getByProductId(productId)
            .onSuccess {
                when (count > 0) {
                    true -> remoteDataSource.patchItemQuantity(it.id, count)
                    false -> remoteDataSource.deleteItem(it.id)
                }
            }.onFailure {
                when (count > 0) {
                    true -> remoteDataSource.postItem(productId)
                        .onSuccess { remoteDataSource.patchItemQuantity(it, count) }
                        .onFailure { return Result.failure(Throwable("Failed to add item")) }
                    false -> return Result.failure(Throwable("Failed to delete item"))
                }
            }
        getAll()
        return Result.success(count)
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        localDataSource.updateChecked(id, checked)
    }

    override fun updateCurrentPageChecked(checked: Boolean) {
        localDataSource.updateCurrentPageChecked(checked)
    }
}
