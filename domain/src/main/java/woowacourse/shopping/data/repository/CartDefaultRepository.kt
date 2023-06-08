package woowacourse.shopping.data.repository

import woowacourse.shopping.data.local.CartLocalDataSource
import woowacourse.shopping.data.remote.CartRemoteDataSource
import woowacourse.shopping.dto.toDomain
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProductPage

class CartDefaultRepository(
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

    override fun getPage(offset: Int, size: Int): Result<CartProductPage> {
        return localDataSource.getPage(offset, size)
    }

    override fun getChecked(): Result<List<CartProduct>> {
        return localDataSource.getChecked()
    }

    override fun getCheckCount(ids: List<Int>): Int {
        return localDataSource.getCheckCount(ids)
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

    override fun updateChecked(ids: List<Int>, checked: Boolean) {
        localDataSource.updateChecked(ids, checked)
    }
}
