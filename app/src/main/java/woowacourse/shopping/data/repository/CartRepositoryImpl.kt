package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartLocalDataSource
import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingInThread
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

    override fun fetchCartProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val result = cartRemoteDataSource.fetchCartItems(page, size).getOrThrow()
        val products = result.content.map { it.toDomain() }
        cartLocalDataSource.addAllCartProducts(products)
        val hasMore = !result.last
        PageableItem(products, hasMore)
    }

    override fun deleteCartProduct(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartRemoteDataSource.deleteCartItem(cartId).getOrThrow()
        cartLocalDataSource.removeCartProductByCartId(cartId)
        Result.success(Unit)
    }

    override fun increaseQuantity(
        productId: Long,
        increaseCount: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val currentQuantity = cartLocalDataSource.getQuantity(productId)
        if (currentQuantity == 0) {
            val newCartId =
                cartRemoteDataSource
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
        val currentQuantity = cartLocalDataSource.getQuantity(productId)
        patchCartItemQuantity(productId, currentQuantity + (-decreaseCount))
    }

    override fun fetchCartProductCount(onResult: (Result<Int>) -> Unit) =
        runCatchingInThread(onResult) {
            val result = cartRemoteDataSource.fetchCartItemCount().getOrThrow()
            result.quantity
        }

    override fun findQuantityByProductId(productId: Long): Result<Int> =
        runCatching {
            cartLocalDataSource.getQuantity(productId)
        }

    override fun findCartProductByProductId(productId: Long): Result<CartProduct> =
        runCatching { requireNotNull(cartLocalDataSource.getCartProduct(productId)) {} }

    override fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>> =
        runCatching {
            productIds.mapNotNull { cartLocalDataSource.getCartProduct(it) }
        }

    override fun getAllCartProducts(): Result<List<CartProduct>> = runCatching { cartLocalDataSource.getCartProducts() }

    private fun patchCartItemQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val cartId = cartLocalDataSource.getCartProduct(productId)?.cartId ?: return
        val updatedQuantity = Quantity(quantity)
        cartRemoteDataSource.patchCartItemQuantity(cartId, updatedQuantity).getOrThrow()
        cartLocalDataSource.updateQuantity(productId, quantity)
    }

    private fun fetchCart() =
        runCatchingInThread {
            val totalElements = cartRemoteDataSource.fetchCartItems(0, 1).getOrThrow().totalElements
            val result = cartRemoteDataSource.fetchCartItems(0, totalElements).getOrThrow().content
            cartLocalDataSource.addAllCartProducts(result.map { it.toDomain() })
        }

    private fun CartItemResponse.toDomain() = CartProduct(cartId, product.toDomain(), quantity)

    private fun addCartProductToCart(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product = productDataSource.fetchProduct(productId).getOrThrow().toDomain()
        val cartProduct = CartProduct(cartId, product, quantity)
        cartLocalDataSource.addCartProduct(cartProduct)
    }
}
