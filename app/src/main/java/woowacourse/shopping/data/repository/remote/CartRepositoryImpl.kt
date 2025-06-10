package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.remote.CartRemoteDataSource
import woowacourse.shopping.data.handleResult
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun fetchTotalCount(): Result<Int> = cartRemoteDataSource.fetchTotalCount()

    override suspend fun fetchPagedCartItems(
        page: Int,
        pageSize: Int?,
    ): Result<List<CartItem>> = cartRemoteDataSource.fetchPagedCartItems(page, pageSize)

    override suspend fun fetchAllCartItems(): Result<List<CartItem>> {
        val totalCount =
            cartRemoteDataSource.fetchTotalCount().getOrThrow()
        return cartRemoteDataSource
            .fetchPagedCartItems(0, totalCount)
            .mapCatching { cartItems ->
                cartLocalDataSource.saveCart(cartItems)
                cartItems
            }
    }

    override suspend fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
    ): Result<Unit> {
        val cartItem =
            findCartItemOrFailure(product.productId).getOrElse { return Result.failure(it) }

        return updateProduct(
            cartId = cartItem.cartId,
            product = product,
            quantity = cartItem.quantity + productQuantity,
        )
    }

    override suspend fun insertProduct(
        product: Product,
        productQuantity: Int,
    ): Result<Unit> =
        handleResult {
            val cartId =
                cartRemoteDataSource.insertCartItem(product.productId, productQuantity).getOrThrow()
            val newCartItem = CartItem(cartId, product, productQuantity)
            cartLocalDataSource.add(newCartItem)
        }

    override suspend fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
    ): Result<Unit> =
        cartRemoteDataSource
            .updateQuantity(cartId, quantity)
            .mapCatching {
                val updatedItem = CartItem(cartId, product, quantity)
                cartLocalDataSource.add(updatedItem)
            }

    override suspend fun increaseQuantity(productId: Long): Result<Unit> {
        val cartItem = findCartItemOrFailure(productId).getOrElse { return Result.failure(it) }

        return cartRemoteDataSource
            .updateQuantity(cartItem.cartId, cartItem.quantity + 1)
            .mapCatching {
                cartLocalDataSource.add(cartItem.copy(quantity = cartItem.quantity + 1))
            }
    }

    override suspend fun decreaseQuantity(productId: Long): Result<Unit> {
        val cartItem = findCartItemOrFailure(productId).getOrElse { return Result.failure(it) }

        return if (cartItem.quantity == 1) {
            deleteProduct(productId)
        } else {
            cartRemoteDataSource.updateQuantity(cartItem.cartId, cartItem.quantity - 1)
            cartLocalDataSource.add(cartItem.copy(quantity = cartItem.quantity - 1))
        }
    }

    override suspend fun deleteProduct(productId: Long): Result<Unit> {
        val cartItem = findCartItemOrFailure(productId).getOrElse { return Result.failure(it) }

        return cartRemoteDataSource
            .deleteCartItemById(cartItem.cartId)
            .mapCatching { cartLocalDataSource.delete(productId) }
    }

    override fun getCartItemById(productId: Long): Result<CartItem?> = cartLocalDataSource.find(productId)

    private inline fun <T : Any> Result<T?>.getOrFailureIfNull(exception: () -> Throwable): Result<T> =
        fold(
            onSuccess = { value ->
                if (value != null) {
                    Result.success(value)
                } else {
                    Result.failure(exception())
                }
            },
            onFailure = { throwable -> Result.failure(throwable) },
        )

    private fun findCartItemOrFailure(productId: Long): Result<CartItem> =
        cartLocalDataSource
            .find(productId)
            .getOrFailureIfNull { NoSuchElementException("장바구니에 해당 상품이 없습니다.") }
}
