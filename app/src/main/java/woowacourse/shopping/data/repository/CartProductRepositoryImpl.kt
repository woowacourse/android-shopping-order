package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.CartProductRemoteDataSource
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class CartProductRepositoryImpl(
    private val remoteDataSource: CartProductRemoteDataSource,
) : CartProductRepository {
    override suspend fun insert(
        productId: Int,
        quantity: Int,
    ): Result<Int> {
        return remoteDataSource.insert(productId, quantity)
    }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> {
        return remoteDataSource.getPagedProducts(page = page, size = size)
    }

    override suspend fun getCartProductByProductId(productId: Int): Result<CartProduct?> {
        return getPagedProducts().mapCatching { pagedResult ->
            pagedResult.items.firstOrNull { it.product.id == productId }
        }
    }

    override suspend fun getTotalQuantity(): Result<Int> {
        return remoteDataSource.getTotalQuantity()
    }

    override suspend fun updateQuantity(
        cartProduct: CartProduct,
        newQuantity: Int,
    ): Result<Unit> {
        val result =
            when {
                newQuantity == 0 -> delete(cartProduct.id)
                else -> {
                    remoteDataSource.updateQuantity(cartProduct.id, newQuantity)
                }
            }

        return result
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return remoteDataSource.delete(id)
    }

    override suspend fun deleteProductsByIds(ids: Set<Int>): Result<Unit> {
        return remoteDataSource.deleteProductsByIds(ids)
    }
}
