package woowacourse.shopping.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
    private val cartLocalDataSource: CartLocalDataSource,
    private val productDataSource: ProductRemoteDataSource,
) : CartRepository {
    init {
        fetchCart()
    }

    override suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<CartProduct>> =
        runCatchingDebugLog {
            val result = cartRemoteDataSource.fetchCartItems(page, size).getOrThrow()
            val products = result.content.map { it.toDomain() }
            cartLocalDataSource.addAllCartProducts(products)
            val hasMore = !result.last
            PageableItem(products, hasMore)
        }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> =
        runCatchingDebugLog {
            cartRemoteDataSource.deleteCartItem(cartId).getOrThrow()
            cartLocalDataSource.removeCartProductByCartId(cartId)
            Result.success(Unit)
        }

    override suspend fun increaseQuantity(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit> =
        runCatchingDebugLog {
            val currentQuantity = cartLocalDataSource.getQuantity(productId)
            if (currentQuantity == 0) {
                addCartProductToCart(productId, increaseCount)
                return@runCatchingDebugLog
            }

            patchCartItemQuantity(productId, currentQuantity + increaseCount)
        }

    override suspend fun decreaseQuantity(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit> =
        runCatchingDebugLog {
            val currentQuantity = cartLocalDataSource.getQuantity(productId)
            patchCartItemQuantity(productId, currentQuantity + (-decreaseCount))
        }

    override suspend fun fetchCartProductCount(): Result<Int> =
        runCatchingDebugLog { cartRemoteDataSource.fetchCartItemCount().getOrThrow().quantity }

    override suspend fun findQuantityByProductId(productId: Long): Result<Int> =
        runCatchingDebugLog { cartLocalDataSource.getQuantity(productId) }

    override suspend fun findCartProductByProductId(productId: Long): Result<CartProduct> =
        runCatchingDebugLog { requireNotNull(cartLocalDataSource.getCartProduct(productId)) {} }

    override suspend fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        runCatchingDebugLog { productIds.mapNotNull { cartLocalDataSource.getCartProduct(it) } }

    override suspend fun getAllCartProducts(): Result<List<CartProduct>> = runCatchingDebugLog { cartLocalDataSource.getCartProducts() }

    private suspend fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartId = cartLocalDataSource.getCartProduct(productId)?.cartId ?: return
        val updatedQuantity = Quantity(quantity)
        cartRemoteDataSource.patchCartItemQuantity(cartId, updatedQuantity).getOrThrow()
        cartLocalDataSource.updateQuantity(productId, quantity)
    }

    private fun fetchCart() =
        runCatchingDebugLog {
            CoroutineScope(Dispatchers.IO).launch {
                val totalElements =
                    cartRemoteDataSource.fetchCartItems(0, 1).getOrThrow().totalElements
                val result =
                    cartRemoteDataSource.fetchCartItems(0, totalElements).getOrThrow().content
                cartLocalDataSource.addAllCartProducts(result.map { it.toDomain() })
            }
        }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)

    private suspend fun addCartProductToCart(
        productId: Long,
        quantity: Int,
    ) {
        val requestBody = AddCartItemCommand(productId, quantity)
        val newCartId = cartRemoteDataSource.addCartItem(requestBody).getOrThrow()
        val product = productDataSource.fetchProduct(productId).getOrThrow().toDomain()
        val cartProduct = CartProduct(newCartId, product, quantity)
        cartLocalDataSource.addCartProduct(cartProduct)
    }
}
