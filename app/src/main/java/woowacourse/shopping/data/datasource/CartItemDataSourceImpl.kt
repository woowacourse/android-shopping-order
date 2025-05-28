package woowacourse.shopping.data.datasource

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemResponse
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.data.service.CartItemService

class CartItemDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartItemDataSource {
    override fun fetchCartItems(
        page: Int,
        size: Int,
        onResult: (CartItemResponse?) -> Unit,
    ) {
        cartItemService.getCartItems(page = page, size = size).enqueue(
            object : Callback<CartItemResponse> {
                override fun onResponse(
                    call: Call<CartItemResponse>,
                    response: Response<CartItemResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        onResult(body)
                    }
                }

                override fun onFailure(
                    call: Call<CartItemResponse>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    override fun submitCartItem(
        cartItem: CartItemRequest,
        callback: () -> Unit,
    ) {
        cartItemService.postCartItem(cartItem).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        callback()
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    Log.wtf("asdf", "$t")
                    println("error : $t")
                }
            },
        )
    }

    override fun removeCartItem(
        id: Long,
        callback: (Long) -> Unit,
    ) {
        cartItemService.deleteCartItem(id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        callback(id)
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    override fun updateCartItem(
        id: Long,
        quantity: Quantity,
        onResult: (Long) -> Unit,
    ) {
        cartItemService.patchCartItem(id, quantity).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        onResult(id)
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    override fun fetchCartItemsCount(onResult: (Quantity?) -> Unit) {
        cartItemService.getCartItemsCount().enqueue(
            object : Callback<Quantity> {
                override fun onResponse(
                    call: Call<Quantity>,
                    response: Response<Quantity>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        onResult(body)
                    }
                }

                override fun onFailure(
                    call: Call<Quantity>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }
}
