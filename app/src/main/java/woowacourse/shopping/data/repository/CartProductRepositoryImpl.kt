package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.CartProductRemoteDataSource
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import kotlin.concurrent.thread

class CartProductRepositoryImpl(
    private val remoteDataSource: CartProductRemoteDataSource,
) : CartProductRepository {
    override fun insert(
        productId: Int,
        quantity: Int,
        onSuccess: (Int) -> Unit,
    ) {
        remoteDataSource.insert(productId, quantity, onSuccess)
    }

    override fun getPagedProducts(
        page: Int?,
        size: Int?,
        onSuccess: (PagedResult<CartProduct>) -> Unit,
    ) {
        remoteDataSource.getPagedProducts(page = page, size = size, onSuccess)
    }

    override fun getCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct?) -> Unit,
    ) {
        getPagedProducts { result ->
            val cartProduct = result.items.firstOrNull { it.product.id == productId }
            onSuccess(cartProduct)
        }
    }

    override fun getTotalQuantity(onSuccess: (Int) -> Unit) {
        remoteDataSource.getTotalQuantity(onSuccess)
    }

    override fun updateQuantity(
        productId: Int,
        quantityToAdd: Int,
        onSuccess: () -> Unit,
    ) {
        getCartProductByProductId(productId) { cartProduct ->
            val newQuantity = cartProduct?.quantity?.plus(quantityToAdd) ?: quantityToAdd
            when {
                cartProduct == null -> insert(productId, newQuantity) { onSuccess() }
                newQuantity == 0 -> delete(cartProduct.id) { onSuccess() }
                else -> remoteDataSource.updateQuantity(cartProduct.id, newQuantity) { onSuccess() }
            }
        }
    }

    override fun delete(
        id: Int,
        onSuccess: () -> Unit,
    ) {
        thread {
            remoteDataSource.delete(id, onSuccess)
        }
    }
}
