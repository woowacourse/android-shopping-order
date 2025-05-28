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
        cartProduct: CartProduct,
        quantityToAdd: Int,
        onSuccess: () -> Unit,
    ) {
        val newQuantity = cartProduct.quantity + quantityToAdd
        when {
            newQuantity == 0 -> delete(cartProduct.id) { onSuccess() }
            else ->
                remoteDataSource.updateQuantity(
                    cartProduct.id,
                    newQuantity,
                ) { onSuccess() }
        }
        return
    }

    override fun delete(
        id: Int,
        onSuccess: () -> Unit,
    ) {
        remoteDataSource.delete(id, onSuccess)
    }
}
