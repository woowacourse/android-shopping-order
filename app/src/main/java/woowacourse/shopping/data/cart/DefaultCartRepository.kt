package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.datasource.CartDataSource
import woowacourse.shopping.data.cart.model.CartPageData
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val cartDataSource: CartDataSource,
) : CartRepository {
    private var cartPageData: CartPageData? = null
    private var cachedCart: Cart = Cart()

    override suspend fun findCartProduct(productId: Long): Result<CartProduct> {
        if (cachedCart.isEmpty()) loadCart()
        val cartProduct =
            cachedCart.findCartProductByProductId(productId)
                ?: return Result.failure(NoSuchElementException("there's no such product"))
        return Result.success(cartProduct)
    }

    override fun existsCartProduct(productId: Long): Boolean {
        return cachedCart.hasProductId(productId)
    }

    override suspend fun loadCurrentPageCart(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart> {
        return cartDataSource
            .loadCarts(currentPage, pageSize)
            .concatCart()
    }

    override suspend fun loadCart(): Result<Cart> {
        return cartDataSource.loadTotalCarts()
            .toCart()
    }

    override suspend fun filterCartProducts(productIds: List<Long>): Result<Cart> {
        return runCatching {
            if (cachedCart.isEmpty()) loadCart()
            cachedCart.filterByProductIds(productIds)
        }
    }

    override suspend fun createCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart> {
        val cartId =
            cartDataSource.createCartProduct(product.id, count)
                .onFailure { return Result.failure(it) }
                .getOrThrow()
        val newCartProduct = CartProduct(product, count, cartId)
        cachedCart = cachedCart.add(newCartProduct)
        return Result.success(cachedCart)
    }

    override suspend fun updateCartProduct(
        product: Product,
        count: Int,
    ): Result<Cart> {
        val cartProduct =
            findCartProduct(product.id).onFailure {
                return Result.failure(it)
            }.getOrThrow()
        return cartDataSource.updateCartCount(cartProduct.id, count)
            .onFailure {
                return Result.failure(it)
            }.map {
                val newCartProduct = cartProduct.copy(count = count)
                cachedCart.add(newCartProduct).also { cachedCart = it }
            }
    }

    override suspend fun deleteCartProduct(productId: Long): Result<Cart> {
        val cartProduct =
            findCartProduct(productId).onFailure {
                return Result.failure(it)
            }.getOrThrow()
        val cartId = cartProduct.id
        return cartDataSource.deleteCartProduct(cartId)
            .onFailure {
                return Result.failure(it)
            }.map {
                cachedCart.delete(productId).also { cachedCart = it }
            }
    }

    override fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean> {
        if (currentPage < 0) return Result.success(false)
        val cartPageData =
            cartPageData ?: return Result.failure(
                NoSuchElementException("there's no any CartProducts"),
            )
        val minSize = currentPage * pageSize
        return Result.success(cartPageData.totalProductSize > minSize)
    }

    override fun clearCart() {
        cachedCart = Cart()
        cartPageData = null
    }

    private fun Result<CartPageData>.toCart(): Result<Cart> {
        return mapCatching { cartData ->
            cartPageData = cartData
            cartData.toDomain().also { newCart ->
                cachedCart = newCart
            }
        }
    }

    private fun Result<CartPageData>.concatCart(): Result<Cart> {
        return mapCatching { cartData ->
            cartPageData = cartData
            cartData.toDomain().also { newCart ->
                cachedCart = cachedCart.addAll(newCart)
            }
        }
    }
}
