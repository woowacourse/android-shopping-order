package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartLocalDataSource
import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val cartDataSource: CartRemoteDataSource,
    private val productDataSource: ProductRemoteDataSource,
) : CartRepository {
    override suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ) = runCatching {
        val result = cartDataSource.fetchCartItems(page, size).getOrThrow()
        val products = result.content.map { it.toDomain() }
        val hasMore = !result.last
        PageableItem(products, hasMore)
    }

    override fun fetchCartProductByProductId(productId: Long): Result<CartProduct> =
        runCatching {
            cartLocalDataSource.fetchCartProductByProductId(productId)
        }

    override fun fetchAllCartItems(): Result<List<CartProduct>> = runCatching { cartLocalDataSource.cachedCartProducts }

    override fun fetchCartIdByProductId(productId: Long): Result<Long> =
        runCatching {
            cartLocalDataSource.fetchCartIdByProductId(productId)
        }

    override suspend fun deleteCartItem(cartId: Long) =
        runCatching {
            cartLocalDataSource.deleteCartProductFromCartByCartId(cartId)
            cartDataSource.deleteCartItem(cartId).getOrThrow()
        }

    override suspend fun deleteCartItems(cartIds: List<Long>): Result<Unit> =
        runCatching {
            cartIds.forEach { cartId ->
                cartLocalDataSource.deleteCartProductFromCartByCartId(cartId)
                cartDataSource.deleteCartItem(cartId).getOrThrow()
            }
        }

    override suspend fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit> =
        runCatching {
            val currentQuantity = cartLocalDataSource.fetchQuantityByProductId(productId)
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
            val currentQuantity = cartLocalDataSource.fetchQuantityByProductId(productId)
            patchCartItemQuantity(productId, currentQuantity + (-decreaseCount))
        }

    override suspend fun fetchCartItemCount(): Result<Int> =
        runCatching {
            val result = cartDataSource.fetchCartItemCount().getOrThrow()
            result.quantity
        }

    override suspend fun hasCartItem(): Result<Boolean> =
        runCatching {
            val result = cartDataSource.fetchCartItemCount().getOrThrow()
            result.quantity >= 0
        }

    override fun fetchCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        runCatching {
            productIds.mapNotNull { cartLocalDataSource.fetchCartProductByProductId(it) }
        }

    override fun fetchCartProductsByIds(cartIds: List<Long>): Result<List<CartProduct>> =
        runCatching {
            cartIds.mapNotNull { cartLocalDataSource.fetchCartProductByCartId(it) }
        }

    private suspend fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartId = cartLocalDataSource.fetchCartIdByProductId(productId)
        val updatedQuantity = Quantity(quantity)
        cartDataSource.patchCartItemQuantity(cartId, updatedQuantity).getOrThrow()
        cartLocalDataSource.updateQuantityByProductId(productId, quantity)
    }

    override suspend fun fetchCart() {
        val totalElements = cartDataSource.fetchCartItems(0, 1).getOrThrow().totalElements
        val result = cartDataSource.fetchCartItems(0, totalElements).getOrThrow().content
        return cartLocalDataSource.addAll(result.map { it.toDomain() })
    }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)

    private suspend fun addCartProductToCart(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product = productDataSource.fetchProduct(productId).getOrThrow().toDomain()
        val cartProduct = CartProduct(cartId, product, quantity)
        cartLocalDataSource.addCartProductToCart(cartProduct)
    }
}
