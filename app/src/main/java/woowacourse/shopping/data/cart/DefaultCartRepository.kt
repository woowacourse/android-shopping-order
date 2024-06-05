package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.datasource.CartDataSource
import woowacourse.shopping.data.cart.model.CartPageData
import woowacourse.shopping.data.cart.order.OrderDataSource
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val cartDataSource: CartDataSource,
    private val orderDataSource: OrderDataSource,
) : CartRepository {
    private var cartPageData: CartPageData? = null
    private var cachedCart: Cart = Cart()

    init {
        loadCart()
    }

    override fun findCartProduct(productId: Long): Result<CartProduct> {
        val cartProduct =
            cachedCart.findCartProductByProductId(productId)
                ?: return Result.failure(NoSuchElementException("there's no such product"))
        return Result.success(cartProduct)
    }

    override fun loadCurrentPageCart(
        currentPage: Int,
        pageSize: Int,
    ): Result<Cart> {
        return cartDataSource
            .loadCarts(currentPage, pageSize)
            .concatCart()
    }

    override fun loadCart(): Result<Cart> {
        return cartDataSource.loadTotalCarts()
            .toCart()
    }

    override fun filterCartProducts(productIds: List<Long>): Result<Cart> {
        return runCatching {
            cachedCart.filterByProductIds(productIds)
        }
    }

    override fun createCartProduct(
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

    override fun updateCartProduct(
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

    override fun deleteCartProduct(productId: Long): Result<Cart> {
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

    override fun orderCartProducts(productIds: List<Long>): Result<Unit> {
        val cart = cachedCart.filterByProductIds(productIds)
        val cartIds = cart.cartProducts.map { it.id }
        return orderDataSource.orderProducts(cartIds).onSuccess {
            loadCart()
        }
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
