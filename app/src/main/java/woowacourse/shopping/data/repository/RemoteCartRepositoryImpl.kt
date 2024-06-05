package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.CartItemDto
import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.data.model.dto.QuantityDto
import woowacourse.shopping.data.model.dto.ShoppingProductDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.CartRepository
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class RemoteCartRepositoryImpl : CartRepository {
    private val service = ProductClient.service

    override fun fetchCartItemsInfo(resultCallback: (Result<List<CartItem>>) -> Unit) {
        service.requestCartItems().enqueue(
            object : Callback<CartItemDto> {
                override fun onResponse(
                    call: Call<CartItemDto>,
                    response: Response<CartItemDto>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val cartItemDto = response.body()
                        cartItemDto?.let { dto ->
                            val cartItems = dto.content.map { it.toDomainModel() }
                            resultCallback(Result.success(cartItems))
                        }
                    } else {
                        resultCallback(Result.failure(RuntimeException("Failed to fetch cart items.")))
                    }
                }

                override fun onFailure(
                    call: Call<CartItemDto>,
                    throwable: Throwable,
                ) {
                    resultCallback(Result.failure(throwable))
                }
            },
        )
    }

    override fun fetchCartItemsInfoWithPage(
        page: Int,
        pageSize: Int,
        resultCallback: (List<CartItem>) -> Unit,
    ) {
        var cartItems: List<CartItem>? = null
        threadAction {
            val response = service.requestCartItems(page, pageSize).execute()
            if (response.isSuccessful && response.body() != null) {
                val cartItemDto = response.body()
                cartItems = cartItemDto?.content?.map { it.toDomainModel() }.orEmpty()
            }
        }
        cartItems?.let {
            resultCallback(it)
        }
    }

    override fun fetchTotalQuantity(resultCallback: (Result<Int>) -> Unit) {
        service.requestCartItemsCount().enqueue(
            object : Callback<QuantityDto> {
                override fun onResponse(
                    call: Call<QuantityDto>,
                    response: Response<QuantityDto>,
                ) {
                    if (response.isSuccessful) {
                        val totalQuantity = response.body()?.quantity ?: 0
                        resultCallback(Result.success(totalQuantity))
                    } else {
                        resultCallback(Result.failure(RuntimeException("Failed to fetch products.")))
                    }
                }

                override fun onFailure(
                    call: Call<QuantityDto>,
                    throwable: Throwable,
                ) {
                    resultCallback(Result.failure(throwable))
                }
            },
        )
    }

    override fun findCartItemWithProductId(productId: Long): CartItem? {
        val cartItems: List<CartItem> = fetchCartItemsWithSync()
        return cartItems.find { it.productId == productId }
    }

    private fun fetchCartItemsWithSync(): List<CartItem> {
        var cartItems: List<CartItem> = emptyList()
        threadAction {
            val response = service.requestCartItems().execute()
            if (response.isSuccessful && response.body() != null) {
                val cartItemDto = response.body()
                cartItems = cartItemDto?.content?.map { it.toDomainModel() }.orEmpty()
            }
        }
        return cartItems
    }

    override fun fetchItemQuantityWithProductId(productId: Long): Int {
        return findCartItemWithProductId(productId)?.quantity ?: 0
    }

    override fun fetchCartItem(cartItemId: Long): CartItem {
        val cartItems: List<CartItem> = fetchCartItemsWithSync()
        return cartItems.find { it.id == cartItemId } ?: throw NoSuchElementException()
    }

    override fun addCartItem(
        productId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit,
    ) {
        val cartItem: CartItem? = findCartItemWithProductId(productId)
        if (cartItem == null) {
            addNewCartItem(productId, quantity, resultCallback)
        } else {
            updateCartItemQuantity(cartItem.id, quantity, resultCallback)
        }
    }

    private fun addNewCartItem(
        productId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit,
    ) {
        service.addCartItem(ShoppingProductDto(productId, quantity))
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        if (response.isSuccessful) {
                            resultCallback(Result.success(Unit))
                        } else {
                            resultCallback(Result.failure(RuntimeException("Failed to add item. Check product Id.")))
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        throwable: Throwable,
                    ) {
                        resultCallback(Result.failure(throwable))
                    }
                },
            )
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit,
    ) {
        service.updateCartItemQuantity(cartItemId, quantity).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        resultCallback(Result.success(Unit))
                    } else {
                        resultCallback(Result.failure(RuntimeException("Failed to update item quantity. Check item id.")))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    throwable: Throwable,
                ) {
                    resultCallback(Result.failure(throwable))
                }
            },
        )
    }

    override fun updateCartItemQuantityWithProductId(
        productId: Long,
        quantity: Int,
        resultCallback: (Result<Unit>) -> Unit,
    ) {
        val cartItemId = findCartItemIdWithProductId(productId)
        if (cartItemId == -1L) {
            addNewCartItem(productId, quantity, resultCallback)
        } else {
            updateCartItemQuantity(cartItemId, quantity, resultCallback)
        }
    }

    private fun findCartItemIdWithProductId(productId: Long): Long {
        val cartItems: List<CartItem> = fetchCartItemsWithSync()
        return cartItems.find { it.productId == productId }?.id ?: -1L
    }

    override fun deleteCartItem(
        cartItemId: Long,
        resultCallback: (Result<Unit>) -> Unit,
    ) {
        service.deleteCartItem(cartItemId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        resultCallback(Result.success(Unit))
                    } else {
                        resultCallback(Result.failure(RuntimeException("Failed to delete item. Check item id.")))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    throwable: Throwable,
                ) {
                    resultCallback(Result.failure(throwable))
                }
            },
        )
    }

    override fun deleteCartItemWithProductId(
        productId: Long,
        resultCallback: (Result<Unit>) -> Unit,
    ) {
        val cartItemId = findCartItemIdWithProductId(productId)
        if (cartItemId != -1L) {
            deleteCartItem(cartItemId, resultCallback)
        }
    }

    override fun deleteAllItems() {
        val cartItems: List<CartItem> = fetchCartItemsWithSync()
        if (cartItems.isEmpty()) {
            return
        } else {
            threadAction {
                cartItems.forEach { item ->
                    service.deleteCartItem(item.id).execute()
                }
            }
        }
    }

    override fun makeOrder(
        order: Order,
        resultCallback: (Result<Unit>) -> Unit,
    ) {
        val cartItemIds = order.map.keys.toList()
        service.makeOrder(CartItemsDto(cartItemIds)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        resultCallback(Result.success(Unit))
                    } else {
                        resultCallback(Result.failure(RuntimeException("Failed to make order. Check Item Ids.")))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    throwable: Throwable,
                ) {
                    resultCallback(Result.failure(throwable))
                }
            },
        )
    }

    private fun threadAction(action: () -> Unit) {
        val latch = CountDownLatch(ACTION_COUNT)
        thread {
            try {
                action()
            } catch (e: Exception) {
                error(e)
            } finally {
                latch.countDown()
            }
        }
        latch.await()
    }

    companion object {
        private const val ACTION_COUNT = 1
    }
}
