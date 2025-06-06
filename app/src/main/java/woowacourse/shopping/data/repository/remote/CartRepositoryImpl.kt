package woowacourse.shopping.data.repository.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.CartDataSource
import woowacourse.shopping.data.dto.cart.toDomain
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartDataSource: CartDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CartRepository {
    private var cachedCart: Cart = Cart()

    override suspend fun fetchTotalCount(): Result<Int> =
        withContext(defaultDispatcher) {
            runCatching {
                cartDataSource.getTotalCount().quantity
            }
        }

    override suspend fun fetchPagedCartItems(
        page: Int,
        pageSize: Int?,
    ): Result<List<CartItem>> =
        withContext(defaultDispatcher) {
            runCatching {
                cartDataSource.getPagedCartItems(page, pageSize).map { it.toDomain() }
            }
        }

    override suspend fun fetchAllCartItems(): Result<Unit> =
        withContext(defaultDispatcher) {
            runCatching {
                val totalCount = cartDataSource.getTotalCount().quantity
                val cartItems =
                    cartDataSource.getPagedCartItems(0, totalCount).map { it.toDomain() }
                cachedCart = Cart(cartItems)
            }
        }

    override fun fetchCartItemById(productId: Long): CartItem? = cachedCart.findCartItem(productId)

    override suspend fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
    ): Result<Unit> =
        runCatching {
            if (cachedCart.exist(product.productId)) {
                val cartItem =
                    cachedCart.findCartItem(product.productId)
                        ?: throw NoSuchElementException("데이터에 문제가 발생했습니다.")
                updateProduct(cartItem.cartId, product, cartItem.quantity + productQuantity)
            } else {
                insertProduct(product, productQuantity)
            }
        }

    override suspend fun insertProduct(
        product: Product,
        productQuantity: Int,
    ): Result<Unit> =
        withContext(defaultDispatcher) {
            runCatching {
                val cartId =
                    cartDataSource.insertCartItem(product.productId, productQuantity).cartId
                val cartItem = CartItem(cartId, product, productQuantity)
                cachedCart = cachedCart.add(cartItem)
            }
        }

    override suspend fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
    ): Result<Unit> =
        withContext(defaultDispatcher) {
            runCatching {
                cartDataSource.updateQuantity(cartId, quantity)
                cachedCart = cachedCart.add(CartItem(cartId, product, quantity))
            }
        }

    override suspend fun increaseQuantity(productId: Long): Result<Unit> =
        withContext(defaultDispatcher) {
            runCatching {
                val cartItem =
                    cachedCart.findCartItem(productId)
                        ?: throw NoSuchElementException("존재하지 않는 ID입니다.")
                cartDataSource.updateQuantity(cartItem.cartId, cartItem.quantity + 1)
                cachedCart = cachedCart.add(cartItem.copy(quantity = cartItem.quantity + 1))
            }
        }

    override suspend fun decreaseQuantity(productId: Long): Result<Unit> =
        withContext(defaultDispatcher) {
            runCatching {
                val cartItem =
                    cachedCart.findCartItem(productId)
                        ?: throw NoSuchElementException("존재하지 않는 ID입니다.")
                if (cartItem.quantity == 1) {
                    val result = deleteProduct(productId)
                    if (result.isFailure) throw result.exceptionOrNull()!!
                } else {
                    cartDataSource.updateQuantity(cartItem.cartId, cartItem.quantity - 1)
                    cachedCart = cachedCart.add(cartItem.copy(quantity = cartItem.quantity - 1))
                }
            }
        }

    override suspend fun deleteProduct(productId: Long): Result<Unit> =
        withContext(defaultDispatcher) {
            runCatching {
                val cartId =
                    cachedCart.findCartItem(productId)?.cartId
                        ?: throw NoSuchElementException("존재하지 않는 ID입니다.")
                cartDataSource.deleteCartItemById(cartId)
                cachedCart = cachedCart.delete(productId)
            }
        }
}
