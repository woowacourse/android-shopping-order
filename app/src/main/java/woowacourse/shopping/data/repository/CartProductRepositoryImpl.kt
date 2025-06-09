package woowacourse.shopping.data.repository

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
    ): Result<Int> {
        return remoteDataSource.insert(productId, quantity)
    }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> {
        return remoteDataSource.getPagedProducts(page = page, size = size)
            .mapCatching { pagedResult ->
                PagedResult(
                    items = pagedResult.items.map { it.toCartProduct() },
                    hasNext = pagedResult.hasNext,
                )
            }
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
        return when {
            newQuantity == 0 -> delete(cartProduct.id)
            else -> {
                remoteDataSource.updateQuantity(cartProduct.id, newQuantity)
            }
        }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return remoteDataSource.delete(id)
    }
}
