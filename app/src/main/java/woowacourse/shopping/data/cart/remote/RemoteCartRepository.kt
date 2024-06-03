package woowacourse.shopping.data.cart.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

object RemoteCartRepository : CartRepository {
    private const val MAX_CART_ITEM_COUNT = 9999999

    override fun findByProductId(
        productId: Int,
        callback: (Result<CartItem?>) -> Unit,
    ) {
        retrofitApi.requestCartItems(page = 0, size = MAX_CART_ITEM_COUNT)
            .enqueue(
                object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body() ?: return
                            val result = body.toCartItems().find { it.productId == productId }
                            callback(Result.success(result))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartResponse>,
                        t: Throwable,
                    ) {
                        callback(Result.failure(t))
                    }
                },
            )
    }

    override fun syncFindByProductId(
        productId: Int,
        totalItemCount: Int,
    ): CartItem? {
        var cartItem: CartItem? = null
        thread {
            val response = retrofitApi.requestCartItems(page = 0, size = totalItemCount).execute()
            val body = response.body()
            cartItem = body?.toCartItems()?.firstOrNull { productId == it.productId }
        }.join()
        return cartItem
    }

    override fun findAll(callback: (Result<List<CartItem>>) -> Unit) {
        retrofitApi.requestCartItems(page = 0, size = MAX_CART_ITEM_COUNT)
            .enqueue(
                object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body() ?: return
                            val result = body.toCartItems()
                            callback(Result.success(result))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartResponse>,
                        t: Throwable,
                    ) {
                        callback(Result.failure(t))
                    }
                },
            )
    }

    override fun delete(
        id: Int,
        callback: (Result<Unit>) -> Unit,
    ) {
        retrofitApi.deleteCartItem(id = id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun add(
        productId: Int,
        quantity: Quantity,
        callback: (Result<Unit>) -> Unit,
    ) {
        retrofitApi.requestCartQuantityCount(
            addCartItemRequest =
                AddCartItemRequest(
                    productId,
                    quantity.count,
                ),
        ).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun changeQuantity(
        id: Int,
        quantity: Quantity,
        callback: (Result<Unit>) -> Unit,
    ) {
        retrofitApi.setCartItemQuantity(id = id, quantity = CartItemQuantityRequest(quantity.count))
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        if (response.isSuccessful) {
                            callback(Result.success(Unit))
                            return
                        }
                    }

                    override fun onFailure(
                        call: Call<Unit>,
                        t: Throwable,
                    ) {
                        callback(Result.failure(t))
                    }
                },
            )
    }

    override fun getTotalQuantity(callback: (Result<Int>) -> Unit) {
        retrofitApi.requestCartQuantityCount().enqueue(
            object : Callback<CountResponse> {
                override fun onResponse(
                    call: Call<CountResponse>,
                    response: Response<CountResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body() ?: return
                        val result = body.quantity
                        callback(Result.success(result))
                    }
                }

                override fun onFailure(
                    call: Call<CountResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun syncGetTotalQuantity(): Int {
        var cartQuantityCount = 0
        thread {
            val response = retrofitApi.requestCartQuantityCount().execute()
            val body = response.body()?.quantity
            cartQuantityCount = body ?: 0
        }.join()
        return cartQuantityCount
    }
}
