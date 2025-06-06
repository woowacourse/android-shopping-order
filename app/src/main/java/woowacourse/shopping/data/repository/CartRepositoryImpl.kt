package woowacourse.shopping.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartDataSource: CartRemoteDataSource,
    private val productDataSource: ProductRemoteDataSource,
) : CartRepository {
    private val cachedCart = Cart()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            fetchCart()
        }
    }

    override suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ) = runCatching {
        val result = cartDataSource.fetchCartItems(page, size).getOrThrow()
        val products = result.content.map { it.toDomain() }
        val hasMore = !result.last
        PageableItem(products, hasMore)
    }

    override fun fetchAllCartItems(): Result<List<CartProduct>> = runCatching { cachedCart.cachedCartProducts }

    override fun findCartIdByProductId(productId: Long): Result<Long> =
        runCatching {
            cachedCart.findCartIdByProductId(productId)
        }

    override suspend fun deleteCartItem(cartId: Long) =
        runCatching {
            cachedCart.deleteCartProductFromCartByCartId(cartId)
            cartDataSource.deleteCartItem(cartId).getOrThrow()
        }

    override suspend fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit> =
        runCatching {
            val currentQuantity = cachedCart.findQuantityByProductId(productId)
            if (currentQuantity == 0) {
                val newCartId =
                    cartDataSource
                        .addCartItem(AddCartItemCommand(productId, increaseCount))
                        .getOrThrow()
                addCartProductToCart(newCartId, productId, increaseCount)
                return@runCatching
            }
            patchCartItemQuantity(productId, currentQuantity + increaseCount)
        }

    override suspend fun decreaseCartProductQuantityFromCart(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit> =
        runCatching {
            val currentQuantity = cachedCart.findQuantityByProductId(productId)
            patchCartItemQuantity(productId, currentQuantity + (-decreaseCount))
        }

    override suspend fun fetchCartItemCount(): Result<Int> =
        runCatching {
            val result = cartDataSource.fetchCartItemCount().getOrThrow()
            result.quantity
        }

    override fun findQuantityByProductId(productId: Long): Result<Int> = runCatching { cachedCart.findQuantityByProductId(productId) }

    override fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        runCatching {
            productIds.mapNotNull { cachedCart.findCartProductByProductId(it) }
        }

    private suspend fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartId = cachedCart.findCartIdByProductId(productId)
        val updatedQuantity = Quantity(quantity)
        cartDataSource.patchCartItemQuantity(cartId, updatedQuantity).getOrThrow()
        cachedCart.updateQuantityByProductId(productId, quantity)
    }

    private suspend fun fetchCart() {
        val totalElements = cartDataSource.fetchCartItems(0, 1).getOrThrow().totalElements
        val result = cartDataSource.fetchCartItems(0, totalElements).getOrThrow().content
        return cachedCart.addAll(result.map { it.toDomain() })
    }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)

    private suspend fun addCartProductToCart(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product = productDataSource.fetchProduct(productId).getOrThrow().toDomain()
        val cartProduct = CartProduct(cartId, product, quantity)
        cachedCart.addCartProductToCart(cartProduct)
    }
}
