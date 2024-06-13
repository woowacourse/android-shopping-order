package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.client.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.remote.model.dto.CartItemsDto
import woowacourse.shopping.data.remote.model.dto.ShoppingProductDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.UpdatedQuantity
import woowacourse.shopping.domain.repository.CartRepository

class RemoteCartRepositoryImpl : CartRepository {
    private val service = ProductClient.service

    override suspend fun fetchCartItemsInfo(): Result<List<CartItem>> {
        val response = service.requestCartItems()
        val cartItemDto = response.body()
        return if (response.isSuccessful && cartItemDto != null) {
            val cartItems = cartItemDto.content.map { it.toDomainModel() }
            Result.success(cartItems)
        } else {
            Result.failure(RuntimeException("Failed to fetch cart items. code: ${response.code()}"))
        }
    }

    override suspend fun fetchCartItemsInfoWithPage(
        page: Int,
        pageSize: Int,
    ): Result<List<CartItem>> {
        val response = service.requestCartItems(page, pageSize)
        val cartItemDto = response.body()
        return if (response.isSuccessful && cartItemDto != null) {
            val cartItems = cartItemDto.content.map { it.toDomainModel() }
            Result.success(cartItems)
        } else {
            Result.failure(RuntimeException("Failed to fetch cart items. code: ${response.code()}"))
        }
    }

    override suspend fun fetchTotalQuantity(): Result<Int> {
        val response = service.requestCartItemsCount()
        return if (response.isSuccessful) {
            val totalQuantity = response.body()?.quantity ?: 0
            Result.success(totalQuantity)
        } else {
            Result.failure(RuntimeException("Failed to fetch total quantity. code: ${response.code()}"))
        }
    }

    override suspend fun findCartItemWithProductId(productId: Long): CartItem? {
        var cartItems: List<CartItem> = emptyList()
        val result = fetchCartItemsInfo()
        result.onSuccess { items ->
            cartItems = items
        }.onFailure {
            return null
        }
        return cartItems.find { it.productId == productId }
    }

    override suspend fun findCartItemsWithProductIds(productIds: Set<Long>): List<CartItem> {
        var cartItems: List<CartItem> = emptyList()
        val result = fetchCartItemsInfo()
        result.onSuccess { items ->
            cartItems = items
        }.onFailure {
            return emptyList()
        }
        return cartItems.filter { productIds.contains(it.productId) }
    }

    override suspend fun fetchItemQuantityWithProductId(productId: Long): Int {
        return findCartItemWithProductId(productId)?.quantity ?: 0
    }

    override suspend fun fetchCartItem(cartItemId: Long): CartItem? {
        var cartItems: List<CartItem> = emptyList()
        val cartItemsResult = fetchCartItemsInfo()
        cartItemsResult.onSuccess {
            cartItems = it
        }
        return cartItems.find { it.id == cartItemId }
    }

    override suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartItem: CartItem? = findCartItemWithProductId(productId)
        return if (cartItem == null) {
            addNewCartItem(productId, quantity)
        } else {
            updateCartItemQuantity(cartItem.id, quantity)
        }
    }

    private suspend fun addNewCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        val response = service.addCartItem(ShoppingProductDto(productId, quantity))
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Failed to add item. Check product Id. code: ${response.code()}"))
        }
    }

    override suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> {
        val response = service.updateCartItemQuantity(cartItemId, quantity)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Failed to update item quantity. Check item id. code: ${response.code()}"))
        }
    }

    override suspend fun updateCartItemQuantityWithProductId(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartItemId = findCartItemIdWithProductId(productId)
        return if (cartItemId == -1L) {
            addNewCartItem(productId, quantity)
        } else {
            updateCartItemQuantity(cartItemId, quantity)
        }
    }

    private suspend fun findCartItemIdWithProductId(productId: Long): Long {
        var cartItemId = -1L
        val cartItemsResult = fetchCartItemsInfo()
        cartItemsResult.onSuccess { cartItems ->
            cartItemId = cartItems.find { it.productId == productId }?.id ?: -1L
        }.onFailure {
            return -1L
        }
        return cartItemId
    }

    override suspend fun deleteCartItem(cartItemId: Long): Result<Unit> {
        val response = service.deleteCartItem(cartItemId)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Failed to delete item. Check item id. code: ${response.code()}"))
        }
    }

    override suspend fun deleteCartItemWithProductId(productId: Long): Result<Unit> {
        val cartItemId = findCartItemIdWithProductId(productId)
        return if (cartItemId != -1L) {
            deleteCartItem(cartItemId)
        } else {
            Result.failure(IllegalStateException("Failed to find cart item. Check product id."))
        }
    }

    override suspend fun deleteAllItems(): Result<Unit> {
        var result: Result<Unit> = Result.success(Unit)
        val cartItemsFetchResult = fetchCartItemsInfo()
        cartItemsFetchResult.onSuccess {
            it.forEach { item ->
                val deleteResult = deleteCartItem(item.id)
                if (deleteResult.isFailure) {
                    result =
                        Result.failure(RuntimeException("Failed to delete item. Check item id."))
                    return@forEach
                }
            }
        }.onFailure {
            return Result.failure(it)
        }
        return result
    }

    override suspend fun makeOrder(order: Order): Result<Unit> {
        val cartItemIds = order.map.keys.toList()
        val response = service.makeOrder(CartItemsDto(cartItemIds))
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Failed to make order. Check Item Ids. code: ${response.code()}"))
        }
    }

    override suspend fun getCartItemsQuantities(productIds: Set<Long>): Result<List<UpdatedQuantity>> {
        val result = fetchCartItemsInfo()
        var updatedQuantities: List<UpdatedQuantity> = emptyList()
        result.onSuccess { cartItems ->
            updatedQuantities = productIds.map { productId ->
                val item = cartItems.find { it.productId == productId }
                if (item != null) {
                    UpdatedQuantity(productId, item.quantity)
                } else {
                    UpdatedQuantity(productId, 0)
                }
            }
        }.onFailure {
            return Result.failure(RuntimeException("Failed to get cart items."))
        }
        return Result.success(updatedQuantities)
    }
}
