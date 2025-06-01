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

    override fun fetchCartProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val result = cartDataSource.fetchCartItems(page, size).getOrThrow()
        val products = result.content.map { it.toDomain() }
        val hasMore = !result.last
        PageableItem(products, hasMore)
    }

    override fun deleteCartProduct(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartDataSource.deleteCartItem(cartId).getOrThrow()
        cachedCart.removeCartProductByCartId(cartId)
        Result.success(Unit)
    }

    override fun increaseQuantity(
        productId: Long,
        increaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val currentQuantity = cachedCart.getQuantity(productId)
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

    override fun decreaseQuantity(
        productId: Long,
        decreaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val currentQuantity = cachedCart.getQuantity(productId)
        patchCartItemQuantity(productId, currentQuantity + (-decreaseCount))
    }

    override fun fetchCartProductCount(onResult: (Result<Int>) -> Unit) =
        runCatchingInThread(onResult) {
            val result = cartDataSource.fetchCartItemCount().getOrThrow()
            result.quantity
        }

    override fun findCartProductByProductId(productId: Long): Result<CartProduct> =
        runCatching { requireNotNull(cachedCart.getCartProduct(productId)) {} }

    override fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        runCatching {
            productIds.mapNotNull { cachedCart.getCartProduct(it) }
        }

    override fun getAllCartProducts(): Result<List<CartProduct>> = runCatching { cachedCart.getCartProducts() }

    private fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartId = cachedCart.getCartProduct(productId)?.cartId ?: return
        val updatedQuantity = Quantity(quantity)
        cartDataSource.patchCartItemQuantity(cartId, updatedQuantity).getOrThrow()
        cachedCart.updateQuantity(productId, quantity)
    }

    private fun fetchCart() =
        thread {
            val totalElements = cartDataSource.fetchCartItems(0, 1).getOrThrow().totalElements
            val result = cartDataSource.fetchCartItems(0, totalElements).getOrThrow().content
            cachedCart.addAllCartProducts(result.map { it.toDomain() })
        }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)

    private fun addCartProductToCart(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product = productDataSource.fetchProduct(productId).getOrThrow().toDomain()
        val cartProduct = CartProduct(cartId, product, quantity)
        cachedCart.addCartProduct(cartProduct)
    }
}
