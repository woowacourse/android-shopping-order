package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.CartDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartDataSource: CartDataSource,
) : CartRepository {
    private var cachedCart: Cart = Cart()

    override fun fetchTotalCount(onResult: (Result<Int>) -> Unit) {
        cartDataSource.getTotalCount { result ->
            onResult(result)
        }
    }

    override fun fetchPagedCartItems(
        page: Int,
        pageSize: Int?,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        cartDataSource.getPagedCartItems(page, pageSize) { cartItems ->
            onResult(cartItems)
        }
    }

    override fun fetchAllCartItems(onResult: (Result<Unit>) -> Unit) {
        cartDataSource.getTotalCount { totalCountResult ->
            val totalCount = totalCountResult.getOrNull() ?: 0

            cartDataSource.getPagedCartItems(0, totalCount) { cartItemsResult ->
                cartItemsResult
                    .onSuccess { cartItems ->
                        cachedCart = Cart(cartItems)
                        onResult(Result.success(Unit))
                    }.onFailure {
                        onResult(Result.failure(it))
                    }
            }
        }
    }

    override fun fetchCartItemById(productId: Long): CartItem? = cachedCart.findCartItem(productId)

    override fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        if (cachedCart.exist(productId = product.productId)) {
            val cartItem =
                cachedCart.findCartItem(product.productId) ?: throw NoSuchElementException("")

            updateProduct(cartItem.cartId, product, cartItem.quantity + productQuantity) {
                onResult(Result.success(Unit))
            }
        } else {
            insertProduct(product, productQuantity) { Unit }
        }
    }

    override fun insertProduct(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Long>) -> Unit,
    ) {
        cartDataSource.insertCartItem(product.productId, productQuantity) { result ->
            val cartId = result.getOrNull() ?: -1L
            val cartItem =
                CartItem(cartId = cartId, product = product, quantity = productQuantity)
            cachedCart = cachedCart.add(cartItem)
            onResult(result)
        }
    }

    override fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartDataSource.updateQuantity(cartId, quantity) { result ->
            result.onSuccess {
                onResult(result)
                cachedCart = cachedCart.add(CartItem(cartId, product, quantity))
            }
        }
    }

    override fun increaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartItem =
            cachedCart.findCartItem(productId) ?: throw NoSuchElementException("존재하지 않는 아이디")
        cartDataSource.updateQuantity(cartItem.cartId, cartItem.quantity + 1) { result ->
            result.onSuccess {
                onResult(result)
                cachedCart = cachedCart.add(cartItem.copy(quantity = cartItem.quantity + 1))
            }
        }
    }

    override fun decreaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartItem =
            cachedCart.findCartItem(productId) ?: throw NoSuchElementException("존재하지 않는 아이디")
        if (cartItem.quantity == 1) {
            deleteProduct(productId) { result ->
                result.onSuccess {
                    onResult(result)
                }
            }
        } else {
            cartDataSource.updateQuantity(cartItem.cartId, cartItem.quantity - 1) { result ->
                result.onSuccess {
                    onResult(result)
                    cachedCart = cachedCart.add(cartItem.copy(quantity = cartItem.quantity - 1))
                }
            }
        }
    }

    override fun deleteProduct(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartId = cachedCart.findCartItem(productId)?.cartId ?: -1
        cartDataSource.deleteCartItemById(cartId) { result ->
            result.onSuccess {
                onResult(result)
                cachedCart = cachedCart.delete(productId)
            }
        }
    }
}
