package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.impl.RemoteCartDataSource
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.domain.result.getOrNull
import woowacourse.shopping.domain.result.transForm

class CartRepositoryImpl(private val dataSource: CartDataSource = RemoteCartDataSource()) :
    CartRepository {
    override suspend fun getCartItem(productId: Long): Result<CartWithProduct, DataError> =
        when (val carts = getAllCartItems()) {
            is Result.Success -> {
                val cartWithProduct = carts.data.firstOrNull { it.product.id == productId }
                if (cartWithProduct != null) {
                    Result.Success(cartWithProduct)
                } else {
                    Result.Error(DataError.NotFound)
                }
            }

            is Result.Error -> Result.Error(carts.error)
        }

    override suspend fun getAllCartItems(): Result<List<CartWithProduct>, DataError> {
        val count =
            dataSource.getCartItemCounts().getOrNull()?.quantity ?: DEFAULT_CART_COUNT
        return dataSource.getCartItems(START_CART_PAGE, count).transForm { it.toCartWithProduct() }
    }

    override suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Result<Unit, DataError> =
        dataSource.postCartItems(RequestCartItemPostDto(productId, quantity))

    override suspend fun deleteCartItem(id: Long): Result<Unit, DataError> =
        dataSource.deleteCartItems(id)

    override suspend fun getCartItemsCount(): Result<Int, DataError> =
        dataSource.getCartItemCounts().transForm { it.quantity }

    override suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit, DataError> =
        dataSource.patchCartItems(
            id = id,
            request = RequestCartItemsPatchDto(quantity),
        )

    override suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit, DataError> =
        when (val carts = getAllCartItems()) {
            is Result.Success -> updateCart(carts.data, productId, quantity)
            is Result.Error -> Result.Error(carts.error)
        }

    private suspend fun CartRepositoryImpl.updateCart(
        carts: List<CartWithProduct>,
        productId: Long,
        quantity: Int,
    ): Result<Unit, DataError> {
        val cart: CartWithProduct? = carts.firstOrNull { it.product.id == productId }
        return if (cart == null) {
            postCartItems(productId, quantity)
        } else {
            patchCartItem(cart.id, cart.quantity.value + quantity)
        }
    }

    companion object {
        private const val DEFAULT_CART_COUNT = 300
        private const val START_CART_PAGE = 0
    }
}
