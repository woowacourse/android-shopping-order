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
    ): Result<Int> = remoteDataSource.insert(productId, quantity)

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> = remoteDataSource.getPagedProducts(page = page, size = size)

    override suspend fun getCartProductByProductId(productId: Int): Result<CartProduct?> {
        getPagedProducts()
            .onSuccess { pagedResult ->
                val cartProduct = pagedResult.items.firstOrNull { it.product.id == productId }
                return Result.success(cartProduct)
            }.onFailure {
                return Result.failure(it)
            }
        return Result.failure(Exception("Unknown error"))
    }

    override suspend fun getTotalQuantity(): Result<Int> = remoteDataSource.getTotalQuantity()

    override suspend fun updateQuantity(
        cartProduct: CartProduct,
        quantityToAdd: Int,
    ): Result<Unit> {
        val newQuantity = cartProduct.quantity + quantityToAdd
        return when {
            newQuantity == 0 -> delete(cartProduct.id)
            else -> remoteDataSource.updateQuantity(cartProduct.id, newQuantity)
        }
    }

    override suspend fun delete(id: Int): Result<Unit> = remoteDataSource.delete(id)
}
