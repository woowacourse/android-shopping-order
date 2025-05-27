package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.CartProductLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartProductRemoteDataSource
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import kotlin.concurrent.thread

class CartProductRepositoryImpl(
    private val remoteDataSource: CartProductRemoteDataSource,
    private val localDataSource: CartProductLocalDataSource,
) : CartProductRepository {
    override fun getPagedProducts(
        page: Int,
        size: Int,
        onSuccess: (PagedResult<CartProduct>) -> Unit,
    ) {
        remoteDataSource.getPagedProducts(page = page, size = size, onSuccess)
    }

    override fun getQuantityByProductId(
        productId: Int,
        onSuccess: (Int?) -> Unit,
    ) {
        thread {
            val result = localDataSource.getQuantityByProductId(productId)
            onSuccess(result)
        }
    }

    override fun getTotalQuantity(onSuccess: (Int) -> Unit) {
        remoteDataSource.getTotalQuantity(onSuccess)
    }

    override fun updateQuantity(
        productId: Int,
        currentQuantity: Int,
        newQuantity: Int,
        onSuccess: () -> Unit,
    ) {
        thread {
            when {
                currentQuantity == newQuantity -> onSuccess()
                newQuantity == 0 -> {
                    deleteByProductId(productId) { onSuccess() }
                }

                currentQuantity == 0 -> {
                    remoteDataSource.insert(productId, newQuantity, onSuccess)
                }

                else -> {
                    localDataSource.updateQuantity(productId, newQuantity)
                    onSuccess()
                }
            }
        }
    }

    override fun deleteByProductId(
        productId: Int,
        onSuccess: () -> Unit,
    ) {
        thread {
            remoteDataSource.deleteByProductId(productId, onSuccess)
        }
    }
}
