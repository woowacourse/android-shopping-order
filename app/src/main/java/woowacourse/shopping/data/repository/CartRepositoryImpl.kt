package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingInThread
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class CartRepositoryImpl(
    private val cartDataSource: CartRemoteDataSource,
    private val productDataSource: ProductRemoteDataSource,
) : CartRepository {
    private val cachedCart = Cart()

    init {
        fetchCart()
    }

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

    override fun deleteCartItem(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartDataSource.deleteCartItem(cartId).getOrThrow()
        cachedCart.deleteCartProductFromCartByCartId(cartId)
        Result.success(Unit)
    }

    override fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val currentQuantity = cachedCart.findQuantityByProductId(productId)
        if (currentQuantity == 0) {
            val newCartId =
                cartDataSource
                    .addCartItem(AddCartItemCommand(productId, increaseCount))
                    .getOrThrow()
            addCartProductToCart(newCartId, productId, increaseCount)
            return@runCatchingInThread
        }

        patchCartItemQuantity(productId, currentQuantity + increaseCount)
    }

    override fun decreaseCartProductQuantityFromCart(
        productId: Long,
        decreaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val currentQuantity = cachedCart.findQuantityByProductId(productId)
        patchCartItemQuantity(productId, currentQuantity + (-decreaseCount))
    }

    override fun fetchCartItemCount(onResult: (Result<Int>) -> Unit) =
        runCatchingInThread(onResult) {
            val result = cartDataSource.fetchCartItemCount().getOrThrow()
            result.quantity
        }

    override fun findQuantityByProductId(productId: Long): Result<Int> = runCatching { cachedCart.findQuantityByProductId(productId) }

    override fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        runCatching {
            productIds.mapNotNull { cachedCart.findCartProductByProductId(it) }
        }

    private fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartId = cachedCart.findCartIdByProductId(productId)
        val updatedQuantity = Quantity(quantity)
        cartDataSource.patchCartItemQuantity(cartId, updatedQuantity).getOrThrow()
        cachedCart.updateQuantityByProductId(productId, quantity)
    }

    private fun fetchCart() =
        thread {
            val totalElements = cartDataSource.fetchCartItems(0, 1).getOrThrow().totalElements
            val result = cartDataSource.fetchCartItems(0, totalElements).getOrThrow().content
            cachedCart.addAll(result.map { it.toDomain() })
        }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)

    private fun addCartProductToCart(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product = productDataSource.fetchProduct(productId).getOrThrow().toDomain()
        val cartProduct = CartProduct(cartId, product, quantity)
        cachedCart.addCartProductToCart(cartProduct)
    }
}
