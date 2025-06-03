package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.CartProductRemoteDataSource
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class CartProductRepositoryImpl(
    private val remoteDataSource: CartProductRemoteDataSource,
) : CartProductRepository {
    override fun insert(
        productId: Int,
        quantity: Int,
        onResult: (Result<Int>) -> Unit,
    ) {
        remoteDataSource.insert(productId, quantity, onResult)
    }

    override fun getPagedProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<PagedResult<CartProduct>>) -> Unit,
    ) {
        remoteDataSource.getPagedProducts(page = page, size = size, onResult)
    }

    override fun getCartProductByProductId(
        productId: Int,
        onResult: (Result<CartProduct?>) -> Unit,
    ) {
        getPagedProducts { result ->
            result
                .onSuccess { pagedResult ->
                    val cartProduct = pagedResult.items.firstOrNull { it.product.id == productId }
                    onResult(Result.success(cartProduct))
                }.onFailure {
                    onResult(Result.failure(it))
                }
        }
    }

    override fun getTotalQuantity(onResult: (Result<Int>) -> Unit) {
        remoteDataSource.getTotalQuantity(onResult)
    }

    override fun updateQuantity(
        cartProduct: CartProduct,
        quantityToAdd: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val newQuantity = cartProduct.quantity + quantityToAdd
        when {
            newQuantity == 0 -> delete(cartProduct.id) { onResult(Result.success(Unit)) }
            else ->
                remoteDataSource.updateQuantity(
                    cartProduct.id,
                    newQuantity,
                ) { onResult(Result.success(Unit)) }
        }
        return
    }

    override fun delete(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        remoteDataSource.delete(id, onResult)
    }
}
