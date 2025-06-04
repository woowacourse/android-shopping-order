package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.CartProductRemoteDataSource
import woowacourse.shopping.data.dto.response.toCartProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class CartProductRepositoryImpl(
    private val remoteDataSource: CartProductRemoteDataSource,
) : CartProductRepository {
    override suspend fun insert(
        productId: Int,
        quantity: Int,
    ): Result<Int> =
        withContext(Dispatchers.IO) {
            remoteDataSource.insert(productId, quantity)
        }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> =
        withContext(Dispatchers.IO) {
            remoteDataSource.getPagedProducts(page = page, size = size).mapCatching { pagedResult ->
                PagedResult(
                    items = pagedResult.items.map { it.toCartProduct() },
                    hasNext = pagedResult.hasNext,
                )
            }
        }

    override suspend fun getCartProductByProductId(productId: Int): Result<CartProduct?> =
        withContext(Dispatchers.IO) {
            getPagedProducts().mapCatching { pagedResult ->
                pagedResult.items.firstOrNull { it.product.id == productId }
            }
        }

    override suspend fun getTotalQuantity(): Result<Int> =
        withContext(Dispatchers.IO) {
            remoteDataSource.getTotalQuantity()
        }

    override suspend fun updateQuantity(
        cartProduct: CartProduct,
        newQuantity: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            when {
                newQuantity == 0 -> delete(cartProduct.id)
                else -> {
                    remoteDataSource.updateQuantity(cartProduct.id, newQuantity)
                }
            }
        }

    override suspend fun delete(id: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            remoteDataSource.delete(id)
        }

    override suspend fun deleteProductsByIds(ids: Set<Int>): Result<Unit> =
        withContext(Dispatchers.IO) {
            remoteDataSource.deleteProductsByIds(ids)
        }
}
