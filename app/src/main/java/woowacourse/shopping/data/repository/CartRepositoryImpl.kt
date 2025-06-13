package woowacourse.shopping.data.repository

import android.util.Log
import woowacourse.shopping.data.datasource.CartLocalDataSource
import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.safeApiCall
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
    ) = safeApiCall {
        val result = cartDataSource.fetchCartItems(page, size)
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
        safeApiCall {
            cartLocalDataSource.deleteCartProductFromCartByCartId(cartId)
            cartDataSource.deleteCartItem(cartId)
        }

    override suspend fun deleteCartItems(cartIds: List<Long>): Result<Unit> =
        safeApiCall {
            cartIds.forEach { cartId ->
                cartLocalDataSource.deleteCartProductFromCartByCartId(cartId)
                cartDataSource.deleteCartItem(cartId)
            }
        }

    override suspend fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit> =
        safeApiCall {
            val currentQuantity = cartLocalDataSource.findQuantityByProductId(productId) ?: 0
            if (currentQuantity == 0) {
                val newCartId =
                    cartDataSource.addCartItem(AddCartItemCommand(productId, increaseCount))
                addCartProductToCart(newCartId, productId, increaseCount)
                return@safeApiCall
            }
            patchCartItemQuantity(productId, currentQuantity + increaseCount)
        }

    override suspend fun decreaseCartProductQuantityFromCart(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit> =
        safeApiCall {
            val currentQuantity = cartLocalDataSource.findQuantityByProductId(productId)
            patchCartItemQuantity(productId, (currentQuantity ?: 0) + (-decreaseCount))
        }

    override suspend fun fetchCartItemCount(): Result<Int> =
        safeApiCall {
            val result = cartDataSource.fetchCartItemCount()
            result.quantity
        }

    override suspend fun hasCartItem(): Result<Boolean> =
        safeApiCall {
            val result = cartDataSource.fetchCartItemCount()
            result.quantity >= 0
        }

    override suspend fun fetchCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        safeApiCall {
            cartLocalDataSource.fetchCartProductsByProductIds(productIds)
        }

    override fun fetchCartProductsByIds(cartIds: List<Long>): Result<List<CartProduct>> =
        runCatching {
            cartIds.map { cartLocalDataSource.fetchCartProductByCartId(it) }
        }

    private suspend fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartId = cartLocalDataSource.fetchCartIdByProductId(productId)
        val updatedQuantity = Quantity(quantity)
        cartDataSource.patchCartItemQuantity(cartId, updatedQuantity)
        cartLocalDataSource.updateQuantityByProductId(productId, quantity)
    }

    override suspend fun fetchCart(): Result<Unit> =
        safeApiCall {
            val totalElements = cartDataSource.fetchCartItems(0, 1).totalElements
            Log.d("12345", totalElements.toString())
            val result = cartDataSource.fetchCartItems(0, totalElements).content
            Log.d("12345", result.toString())
            cartLocalDataSource.addAll(result.map { it.toDomain() })
        }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)

    private suspend fun addCartProductToCart(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product = productDataSource.fetchProduct(productId).toDomain()
        val cartProduct = CartProduct(cartId, product, quantity)
        cartLocalDataSource.addCartProductToCart(cartProduct)
    }
}
