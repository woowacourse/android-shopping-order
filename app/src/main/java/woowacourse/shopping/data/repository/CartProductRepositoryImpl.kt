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
    private var totalCount: Int = 0

    init {
        thread { totalCount = localDataSource.getTotalCount() }.join()
    }

    override fun getPagedProducts(
        page: Int,
        size: Int,
        onSuccess: (PagedResult<CartProduct>) -> Unit,
    ) {
        remoteDataSource.getPagedProducts(page = page, size = size, onSuccess)
    }

    override fun getQuantityByProductId(
        productId: Long,
        onSuccess: (Int?) -> Unit,
    ) {
        thread {
            val result = localDataSource.getQuantityByProductId(productId)
            onSuccess(result)
        }
    }

    override fun getTotalQuantity(onSuccess: (Int) -> Unit) {
        thread {
            val result = localDataSource.getTotalQuantity()
            onSuccess(result)
        }
    }

    override fun updateQuantity(
        productId: Long,
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
                    remoteDataSource.insert(productId.toInt(), newQuantity, onSuccess)
                    totalCount++
                }

                else -> {
                    localDataSource.updateQuantity(productId, newQuantity)
                    onSuccess()
                }
            }
        }
    }

    override fun deleteByProductId(
        productId: Long,
        onSuccess: () -> Unit,
    ) {
        thread {
            remoteDataSource.deleteByProductId(productId.toInt(), onSuccess)
            totalCount--
        }
    }
}
