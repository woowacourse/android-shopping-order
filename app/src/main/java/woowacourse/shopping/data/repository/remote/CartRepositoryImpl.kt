package woowacourse.shopping.data.repository.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun fetchTotalCount(): Result<Int> = withContext(Dispatchers.IO) { cartRemoteDataSource.fetchTotalCount() }

    override suspend fun fetchPagedCartItems(
        page: Int,
        pageSize: Int?,
    ): Result<List<CartItem>> = withContext(Dispatchers.IO) { cartRemoteDataSource.fetchPagedCartItems(page, pageSize) }

    override suspend fun fetchAllCartItems(): Result<List<CartItem>> =
        withContext(Dispatchers.IO) {
            cartRemoteDataSource.fetchTotalCount().fold(
                onSuccess = { totalCount ->
                    cartRemoteDataSource.fetchPagedCartItems(0, totalCount).fold(
                        onSuccess = { cartItems ->
                            cartLocalDataSource.saveCart(cartItems)
                            Result.success(cartItems)
                        },
                        onFailure = { throwable ->
                            Result.failure(throwable)
                        },
                    )
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                },
            )
        }

    override suspend fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            if (cartLocalDataSource.exist(product.productId)) {
                val result = findCartItemOrFail(product.productId)
                result.fold(
                    onSuccess = { cartItem ->
                        updateProduct(
                            cartId = cartItem.cartId,
                            product = product,
                            quantity = cartItem.quantity + productQuantity,
                        )
                    },
                    onFailure = { throwable ->
                        Result.failure(throwable)
                    },
                )
            } else {
                insertProduct(product, productQuantity).map { Unit }
            }
        }

    override suspend fun insertProduct(
        product: Product,
        productQuantity: Int,
    ): Result<Long> =
        withContext(Dispatchers.IO) {
            cartRemoteDataSource
                .insertCartItem(product.productId, productQuantity)
                .mapCatching { cartId ->
                    val cartItem =
                        CartItem(
                            cartId = cartId,
                            product = product,
                            quantity = productQuantity,
                        )
                    cartLocalDataSource.add(cartItem)
                    cartId
                }
        }

    override suspend fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            cartRemoteDataSource
                .updateQuantity(cartId, quantity)
                .mapCatching {
                    val updatedCartItem = CartItem(cartId, product, quantity)
                    cartLocalDataSource.add(updatedCartItem)
                }
        }

    override suspend fun increaseQuantity(productId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            findCartItemOrFail(productId).fold(
                onSuccess = { cartItem ->
                    cartRemoteDataSource
                        .updateQuantity(cartItem.cartId, cartItem.quantity + 1)
                        .mapCatching {
                            cartLocalDataSource.add(cartItem.copy(quantity = cartItem.quantity + 1))
                        }
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                },
            )
        }

    override suspend fun decreaseQuantity(productId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            findCartItemOrFail(productId).fold(
                onSuccess = { cartItem ->
                    if (cartItem.quantity == 1) {
                        deleteProduct(productId)
                    } else {
                        cartRemoteDataSource
                            .updateQuantity(cartItem.cartId, cartItem.quantity - 1)
                            .mapCatching {
                                cartLocalDataSource.add(cartItem.copy(quantity = cartItem.quantity - 1))
                            }
                    }
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                },
            )
        }

    override suspend fun deleteProduct(productId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            findCartItemOrFail(productId).fold(
                onSuccess = { cartItem ->
                    cartRemoteDataSource
                        .deleteCartItemById(cartItem.cartId)
                        .mapCatching { cartLocalDataSource.delete(productId) }
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                },
            )
        }

    override fun getCartItemById(productId: Long): CartItem? = cartLocalDataSource.find(productId)

    private fun findCartItemOrFail(productId: Long): Result<CartItem> {
        val cartItem = cartLocalDataSource.find(productId)
        return cartItem?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("해당 상품을 찾을 수 없습니다."))
    }
}
