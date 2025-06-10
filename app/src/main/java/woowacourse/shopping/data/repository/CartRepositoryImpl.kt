package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.result.flatMapCatching
import woowacourse.shopping.data.util.result.mapCatchingDebugLog
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
    private val cartLocalDataSource: CartLocalDataSource,
    private val productRemoteDataSource: ProductRemoteDataSource,
) : CartRepository {
    override suspend fun fetchCart(): Result<Unit> =
        fetchAllCartItems()
            .mapCatchingDebugLog { items -> items.map { it.toDomain() } }
            .flatMapCatching { cartProducts -> cartLocalDataSource.addAllCartProducts(cartProducts) }

    override suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<CartProduct>> =
        cartRemoteDataSource
            .fetchCartItems(page, size)
            .mapCatchingDebugLog { response ->
                val products = response.content.map { it.toDomain() }
                val hasMore = !response.last
                PageableItem(products, hasMore)
            }.mapCatchingDebugLog { pageableItem ->
                cartLocalDataSource.addAllCartProducts(pageableItem.items)
                pageableItem
            }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> =
        cartRemoteDataSource
            .deleteCartItem(cartId)
            .mapCatchingDebugLog { cartLocalDataSource.removeCartProductByCartId(cartId) }

    override suspend fun increaseQuantity(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit> =
        cartLocalDataSource
            .getQuantity(productId)
            .flatMapCatching { currentQuantity ->
                if (currentQuantity == 0) {
                    return@flatMapCatching addCartProductToCart(productId, increaseCount)
                }

                patchCartItemQuantity(productId, currentQuantity + increaseCount)
            }

    override suspend fun decreaseQuantity(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit> =
        cartLocalDataSource
            .getQuantity(productId)
            .flatMapCatching { currentQuantity ->
                patchCartItemQuantity(productId, currentQuantity - decreaseCount)
            }

    override suspend fun fetchCartProductCount(): Result<Int> =
        cartRemoteDataSource
            .fetchCartItemCount()
            .map { it.quantity }

    override suspend fun findQuantityByProductId(productId: Long): Result<Int> =
        cartLocalDataSource.getQuantity(productId).mapCatchingDebugLog { it }

    override suspend fun findCartProductByProductId(productId: Long): Result<CartProduct> =
        cartLocalDataSource
            .getCartProduct(productId)
            .mapCatchingDebugLog { requireNotNull(it) { ERROR_PRODUCT_NOT_FOUND.format(productId) } }

    override suspend fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        runCatchingDebugLog {
            productIds.mapNotNull { cartLocalDataSource.getCartProduct(it).getOrNull() }
        }

    override suspend fun getAllCartProducts(): Result<List<CartProduct>> = cartLocalDataSource.getCartProducts().mapCatchingDebugLog { it }

    private suspend fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        cartLocalDataSource
            .getCartProduct(productId)
            .mapCatchingDebugLog {
                val cartId = it?.cartId
                requireNotNull(cartId) { ERROR_CART_ID_NOT_FOUND.format(productId) }
            }.mapCatchingDebugLog { cartId ->
                cartRemoteDataSource.patchCartItemQuantity(cartId, Quantity(quantity))
            }.mapCatchingDebugLog {
                cartLocalDataSource.updateQuantity(productId, quantity)
            }

    private suspend fun addCartProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        cartRemoteDataSource
            .addCartItem(AddCartItemCommand(productId, quantity))
            .flatMapCatching { newCartId ->
                createNewCartProduct(newCartId, productId, quantity)
            }.mapCatchingDebugLog { cartProduct ->
                cartLocalDataSource.addCartProduct(cartProduct)
            }

    private suspend fun fetchAllCartItems(): Result<List<CartItemResponse>> =
        cartRemoteDataSource
            .fetchCartItems(0, 1)
            .flatMapCatching { pageableResponse ->
                cartRemoteDataSource.fetchCartItems(0, pageableResponse.totalElements)
            }.map { it.content }

    private suspend fun createNewCartProduct(
        newCartId: Long,
        productId: Long,
        quantity: Int,
    ): Result<CartProduct> =
        productRemoteDataSource
            .fetchProduct(productId)
            .mapCatchingDebugLog { product ->
                CartProduct(newCartId, product.toDomain(), quantity)
            }

    private fun CartItemResponse.toDomain(): CartProduct = CartProduct(cartId, product.toDomain(), quantity)

    companion object {
        private const val ERROR_PRODUCT_NOT_FOUND =
            "장바구니에서 해당 상품을 찾을 수 없습니다. (ProductId: %d)"
        private const val ERROR_CART_ID_NOT_FOUND =
            "상품에 연결된 장바구니 ID가 존재하지 않습니다. (ProductId: %d)"
    }
}
