package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingInThread
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartDataSource: CartRemoteDataSource,
) : CartRepository {
    override fun fetchCartItems(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val result = cartDataSource.fetchCartItems(page, size).getOrThrow()
        val products = result.content.map { it.toDomain() }
        val hasMore = !result.last
        PageableItem(products, hasMore)
    }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartDataSource.addCartItem(AddCartItemCommand(productId, quantity)).getOrThrow()
        Result.success(Unit)
    }

    override fun deleteCartItem(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartDataSource.deleteCartItem(cartId).getOrThrow()
        Result.success(Unit)
    }

    override fun patchCartItemQuantity(
        cartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartDataSource.patchCartItemQuantity(cartId, Quantity(quantity)).getOrThrow()
        Result.success(Unit)
    }

    override fun fetchCartItemCount(onResult: (Result<Int>) -> Unit) =
        runCatchingInThread(onResult) {
            val result = cartDataSource.fetchCartItemCount().getOrThrow()
            result.quantity
        }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)
}
