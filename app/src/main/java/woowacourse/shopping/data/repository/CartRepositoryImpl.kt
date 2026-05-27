package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.localdb.dao.CartItemQuantityDao
import woowacourse.shopping.data.localdb.entity.CartItemQuantityEntity
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.dto.request.AddCartRequestBody
import woowacourse.shopping.data.remote.dto.request.UpdateCartRequestBody
import woowacourse.shopping.data.remote.dto.response.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.response.cart.CartProductResponse
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName
import kotlin.coroutines.cancellation.CancellationException

class CartRepositoryImpl(
    private val cartApi: CartApi,
    private val cartItemQuantityDao: CartItemQuantityDao,
) : CartRepository {
    override suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): Result<CartResponseResult> =
        try {
            val response =
                cartApi.getCartItems(
                    page = page,
                    size = size,
                )
            val cartItems = response.content.map { it.toDomain() }
            val lastPage = response.last
            val totalElement = response.totalElements

            Result.success(CartResponseResult(cartItems, lastPage, totalElement))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun setCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        try {
            require(quantity >= 0) { "수량은 0이상이어야 합니다." }

            val savedCartItem = cartItemQuantityDao.findByProductId(productId)

            if (quantity == 0) {
                savedCartItem?.let {
                    cartApi.deleteCartItem(it.cartItemId)

                    cartItemQuantityDao.deleteByProductId(productId)
                }
                return Result.success(Unit)
            }

            if (savedCartItem == null) {
                cartApi.addCartItem(
                    AddCartRequestBody(
                        productId = productId,
                        quantity = quantity,
                    ),
                )
                refreshCartQuantity()
            } else {
                cartApi.updateCartItem(
                    id = savedCartItem.cartItemId,
                    updateCartRequestBody =
                        UpdateCartRequestBody(
                            quantity = quantity,
                        ),
                )

                cartItemQuantityDao.insert(
                    savedCartItem.copy(
                        quantity = quantity,
                    ),
                )
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun addCartItemQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        try {
            require(quantity > 0) { "추가 수량은 1 이상이어야 합니다." }

            val savedCartItem = cartItemQuantityDao.findByProductId(productId)

            if (savedCartItem == null) {
                cartApi.addCartItem(
                    AddCartRequestBody(
                        productId = productId,
                        quantity = quantity,
                    ),
                )
                refreshCartQuantity()
            } else {
                val nextQuantity = savedCartItem.quantity + quantity

                cartApi.updateCartItem(
                    id = savedCartItem.cartItemId,
                    updateCartRequestBody = UpdateCartRequestBody(quantity = nextQuantity),
                )

                cartItemQuantityDao.insert(
                    savedCartItem.copy(
                        quantity = nextQuantity,
                    ),
                )
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun deleteItem(cartItemId: Long): Result<Unit> =
        try {
            cartApi.deleteCartItem(cartItemId)

            cartItemQuantityDao.deleteByCartItemId(cartItemId)

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getTotalCartItemQuantity(): Result<Int> =
        try {
            val response = cartApi.getCartItemsQuantity().quantity
            Result.success(response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun syncCartQuantity(): Result<Unit> =
        try {
            refreshCartQuantity()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override fun getCartQuantityMap(): Flow<Map<Long, Int>> =
        cartItemQuantityDao.getAll().map { entities ->
            entities.associate { entity ->
                entity.productId to entity.quantity
            }
        }

    private suspend fun refreshCartQuantity() {
        val response =
            cartApi.getCartItems(
                page = 0,
                size = MAX_CART_ITEM_LIMIT,
            )

        val entities =
            response.content.map { cartItem ->
                CartItemQuantityEntity(
                    productId = cartItem.product.id,
                    cartItemId = cartItem.id,
                    quantity = cartItem.quantity,
                )
            }

        cartItemQuantityDao.clear()
        cartItemQuantityDao.insertAll(entities)
    }

    override suspend fun getAllCartItems(): Result<List<CartItem>> =
        try {
            val response =
                cartApi.getCartItems(
                    page = 0,
                    size = MAX_CART_ITEM_LIMIT,
                )

            Result.success(response.content.map { it.toDomain() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun CartItemResponse.toDomain(): CartItem =
        CartItem(
            id = id,
            product = product.toDomain(),
            quantity = quantity,
        )

    private fun CartProductResponse.toDomain(): Product =
        Product(
            id = id,
            name = ProductName(name),
            price = Money(price),
            imageUrl = imageUrl,
            category = category,
        )

    companion object {
        private const val MAX_CART_ITEM_LIMIT = 100
    }
}

data class CartResponseResult(
    val cartItems: List<CartItem>,
    val isLastPage: Boolean,
    val totalElement: Long,
)
