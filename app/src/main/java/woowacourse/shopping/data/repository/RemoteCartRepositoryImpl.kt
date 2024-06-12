package woowacourse.shopping.data.repository

import retrofit2.Response
import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.CartItemDto
import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.data.model.dto.ShoppingProductDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingCart
import woowacourse.shopping.domain.repository.CartRepository

class RemoteCartRepositoryImpl : CartRepository {
    private val service = ProductClient.service
    private var cartItemData: CartItemDto? = null
    private var cartItems: List<CartItem>? = null

    override suspend fun updateCartItems(): Result<Unit> =
        runCatching {
            val response = service.requestCartItems()
            if (response.isSuccessful) {
                cartItemData = (response as Response<CartItemDto>).body()
                cartItems = cartItemData?.content?.map { it.toDomainModel() }
            } else {
                throw IllegalStateException("Failed to update cart items")
            }
        }

    override suspend fun insert(
        product: Product,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val cartId = findCartItemIdWithProductId(product.id)
            cartId.onSuccess {
                if (it == -1L) {
                    val response = service.addCartItem(ShoppingProductDto(product.id, quantity))
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        throw IllegalStateException("Failed to add cart item")
                    }
                } else {
                    val response = service.updateCartItemQuantity(it, quantity)
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        throw IllegalStateException("Failed to update cart item")
                    }
                }
            }
            updateCartItems()
        }

    override suspend fun update(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val response = service.updateCartItemQuantity(productId, quantity)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw IllegalStateException("Failed to update cart item")
            }

            updateCartItems()
        }

    override suspend fun updateQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val response = service.updateCartItemQuantity(cartItemId, quantity)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw IllegalStateException("Failed to update cart item")
            }
            updateCartItems()
        }

    override suspend fun updateQuantityWithProductId(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val contentId = findCartItemIdWithProductId(productId)
            contentId.onSuccess {
                updateQuantity(it, quantity)
            }
        }

    private suspend fun findCartItemIdWithProductId(productId: Long): Result<Long> =
        runCatching {
            val response = service.requestCartItems()
            if (response.isSuccessful) {
                val contentId =
                    response.body()?.content?.find { it.product.id == productId }?.id ?: -1L
                contentId
            } else {
                throw IllegalStateException("Failed to get cart items")
            }
        }

    override suspend fun findQuantityWithProductId(productId: Long): Result<Int> =
        runCatching {
            val cartItems = service.requestCartItems()
            if (cartItems.isSuccessful) {
                val quantity = cartItems.body()?.content?.find { it.product.id == productId }?.quantity ?: 0
                quantity
            } else {
                throw IllegalStateException("Failed to get cart items")
            }
        }

    override suspend fun makeOrder(order: Order): Result<Unit> =
        runCatching {
            val cartItemIds = order.list.map { it.id }
            val response = service.makeOrder(CartItemsDto(cartItemIds))
            if (response.isSuccessful) {
                response.body()
            } else {
                throw IllegalStateException("Failed to make order")
            }
        }

    override suspend fun size(): Result<Int> =
        runCatching {
            cartItemData?.totalElements ?: 0
        }

    override suspend fun sumOfQuantity(): Result<Int> =
        runCatching {
            var quantity: Int = 0

            val response = service.requestCartItemsCount()
            if (response.isSuccessful) {
                quantity = response.body()?.quantity ?: -1
            } else {
                throw IllegalStateException("Failed to get quantity")
            }
            quantity
        }

    override suspend fun findOrNullWithProductId(productId: Long): Result<CartItem?> =
        runCatching {
            val response = service.requestCartItems()
            if (response.isSuccessful) {
                val contentDto =
                    response.body()?.content?.find {
                        it.product.id == productId
                    }
                contentDto?.toDomainModel()
            } else {
                throw IllegalStateException("Failed to get cart items")
            }
        }

    override suspend fun findWithCartItemId(cartItemId: Long): Result<CartItem> =
        runCatching {
            cartItems?.find { it.id == cartItemId } ?: throw NoSuchElementException()
        }

    override suspend fun findAll(): Result<ShoppingCart> =
        runCatching {
            val response = service.requestCartItems()
            if (response.isSuccessful) {
                val cartItems =
                    response.body()?.content?.map {
                        it.toDomainModel()
                    } ?: emptyList()
                ShoppingCart(cartItems ?: emptyList())
            } else {
                throw IllegalStateException("Failed to get cart items")
            }
        }

    override suspend fun findAllPagedItems(
        page: Int,
        pageSize: Int,
    ): Result<ShoppingCart> =
        runCatching {
            var cartItems: List<CartItem> = emptyList()

            val response = service.requestCartItems(page, pageSize)
            if (response.isSuccessful) {
                cartItems = response.body()?.content?.map {
                    it.toDomainModel()
                } ?: emptyList()
            } else {
                throw IllegalStateException("Failed to get cart items")
            }
            ShoppingCart(cartItems)
        }

    override suspend fun delete(cartItemId: Long): Result<Unit> =
        runCatching {
            val response = service.deleteCartItem(cartItemId)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw IllegalStateException("Failed to delete cart item")
            }
            updateCartItems()
        }

    override suspend fun deleteWithProductId(productId: Long): Result<Unit> =
        runCatching {
            val contentId = findCartItemIdWithProductId(productId)
            contentId.onSuccess {
                val response = service.deleteCartItem(it)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    throw IllegalStateException("Failed to delete cart item")
                }
            }
            updateCartItems()
        }

    override suspend fun deleteAll(): Result<Unit> =
        runCatching {
            if (cartItems != null && cartItems!!.isNotEmpty()) {
                cartItems!!.forEach {
                    val response = service.deleteCartItem(it.id)
                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        throw IllegalStateException("Failed to delete cart item")
                    }
                }
            }
            updateCartItems()
        }
}
