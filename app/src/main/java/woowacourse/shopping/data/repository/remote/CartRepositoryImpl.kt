package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
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

    override fun getCartItemById(productId: Long): CartItem? = cartLocalDataSource.find(productId)

    override fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        if (cartLocalDataSource.exist(product.productId)) {
            val cartItem = findCartItemOrFail(product.productId, onResult) ?: return

            updateProduct(cartItem.cartId, product, cartItem.quantity + productQuantity) { result ->
                onResult(result)
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
                    cartLocalDataSource.add(cartItem)
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
                    cartLocalDataSource.add(CartItem(cartId, product, quantity))
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
        val cartItem = findCartItemOrFail(productId, onResult) ?: return

        cartRemoteDataSource.updateQuantity(cartItem.cartId, cartItem.quantity + 1) { result ->
            result.fold(
                onSuccess = {
                    cartLocalDataSource.add(cartItem.copy(quantity = cartItem.quantity + 1))
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
        val cartItem = findCartItemOrFail(productId, onResult) ?: return

        if (cartItem.quantity == 1) {
            deleteProduct(productId) { result ->
                onResult(result)
            }
        } else {
            cartRemoteDataSource.updateQuantity(cartItem.cartId, cartItem.quantity - 1) { result ->
                result.fold(
                    onSuccess = {
                        cartLocalDataSource.add(cartItem.copy(quantity = cartItem.quantity - 1))
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
        val cartItem = findCartItemOrFail(productId, onResult) ?: return

        cartRemoteDataSource.deleteCartItemById(cartItem.cartId) { result ->
            result.fold(
                onSuccess = {
                    cartLocalDataSource.delete(productId)
                    onResult(Result.success(Unit))
                },
                onFailure = { throwable -> onResult(Result.failure(throwable)) },
            )
        }
    }

    override fun fetchAllCartItems(onResult: (Result<List<CartItem>>) -> Unit) {
        cartRemoteDataSource.fetchTotalCount { countResult ->
            countResult.fold(
                onSuccess = { totalCount ->
                    cartRemoteDataSource.fetchPagedCartItems(0, totalCount) { pagedResult ->
                        pagedResult.fold(
                            onSuccess = { cartItems ->
                                cartLocalDataSource.saveCart(cartItems)
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

    private fun findCartItemOrFail(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ): CartItem? {
        val cartItem = cartLocalDataSource.find(productId)
        if (cartItem == null) {
            onResult(Result.failure(NoSuchElementException("해당 상품을 찾을 수 없습니다.")))
            return null
        }
        return cartItem
    }
}
