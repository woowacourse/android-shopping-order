package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    private var cachedCart: Cart = Cart()

    override fun fetchTotalCount(onResult: (Result<Int>) -> Unit) {
        cartRemoteDataSource.fetchTotalCount { result ->
            onResult(result)
        }
    }

    override fun fetchPagedCartItems(
        page: Int,
        pageSize: Int?,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        cartRemoteDataSource.fetchPagedCartItems(page, pageSize) { result ->
            result.fold(
                onSuccess = { cartItems -> onResult(Result.success(cartItems)) },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }

    override fun getCartItemById(productId: Long): CartItem? = cachedCart.findCartItem(productId)

    override fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        if (cachedCart.exist(productId = product.productId)) {
            val cartItem = cachedCart.findCartItem(product.productId)
            if (cartItem == null) {
                onResult(Result.failure(NoSuchElementException("해당 상품을 찾을 수 없습니다.")))
                return
            }

            updateProduct(cartItem.cartId, product, cartItem.quantity + productQuantity) {
                onResult(Result.success(Unit))
            }
        } else {
            insertProduct(product, productQuantity) { result -> onResult(result.map { Unit }) }
        }
    }

    override fun insertProduct(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Long>) -> Unit,
    ) {
        cartRemoteDataSource.insertCartItem(product.productId, productQuantity) { result ->
            result.fold(
                onSuccess = { cartId ->
                    val cartItem =
                        CartItem(cartId = cartId, product = product, quantity = productQuantity)
                    cachedCart = cachedCart.add(cartItem)
                    onResult(Result.success(cartId))
                },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }

    override fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartRemoteDataSource.updateQuantity(cartId, quantity) { result ->
            result.fold(
                onSuccess = {
                    cachedCart = cachedCart.add(CartItem(cartId, product, quantity))
                    onResult(result)
                },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }

    override fun increaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartItem = cachedCart.findCartItem(productId)
        if (cartItem == null) {
            onResult(Result.failure(NoSuchElementException("존재하지 않는 상품입니다")))
            return
        }

        cartRemoteDataSource.updateQuantity(cartItem.cartId, cartItem.quantity + 1) { result ->
            result.fold(
                onSuccess = {
                    cachedCart = cachedCart.add(cartItem.copy(quantity = cartItem.quantity + 1))
                    onResult(Result.success(Unit))
                },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }

    override fun decreaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartItem = cachedCart.findCartItem(productId)
        if (cartItem == null) {
            onResult(Result.failure(NoSuchElementException("존재하지 않는 상품입니다")))
            return
        }

        if (cartItem.quantity == 1) {
            deleteProduct(productId) { result ->
                onResult(result)
            }
        } else {
            cartRemoteDataSource.updateQuantity(cartItem.cartId, cartItem.quantity - 1) { result ->
                result.fold(
                    onSuccess = {
                        cachedCart = cachedCart.add(cartItem.copy(quantity = cartItem.quantity - 1))
                        onResult(Result.success(Unit))
                    },
                    onFailure = { throwable ->
                        onResult(Result.failure(throwable))
                    },
                )
            }
        }
    }

    override fun deleteProduct(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartItem = cachedCart.findCartItem(productId)
        if (cartItem == null) {
            onResult(Result.failure(NoSuchElementException("존재하지 않는 상품입니다")))
            return
        }

        cartRemoteDataSource.deleteCartItemById(cartItem.cartId) { result ->
            result.fold(
                onSuccess = {
                    cachedCart = cachedCart.delete(productId)
                },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
            onResult(result)
        }
    }

    fun fetchAllCartItems(onResult: (Result<List<CartItem>>) -> Unit) {
        cartRemoteDataSource.fetchTotalCount { countResult ->
            countResult.fold(
                onSuccess = { totalCount ->
                    cartRemoteDataSource.fetchPagedCartItems(0, totalCount) { pagedResult ->
                        pagedResult.fold(
                            onSuccess = { cartItems ->
                                cachedCart = Cart(cartItems)
                                onResult(Result.success(cartItems))
                            },
                            onFailure = { throwable -> onResult(Result.failure(throwable)) },
                        )
                    }
                },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }
}
