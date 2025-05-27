package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.data.service.CartItemService

class CartItemDataSourceImpl(private val cartItemService: CartItemService) : CartItemDataSource {
    override fun fetchCartItems(
        page: Int,
        size: Int,
        onResult: (CartItemsResponse?) -> Unit,
    ) {
        cartItemService.getCartItems(page = page, size = size).enqueue(
            object : Callback<CartItemsResponse> {
                override fun onResponse(
                    call: Call<CartItemsResponse>,
                    response: Response<CartItemsResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        onResult(body)
                    }
                }

                override fun onFailure(
                    call: Call<CartItemsResponse>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    override fun submitCartItem(cartItem: CartItemRequest) {
        cartItemService.postCartItem(cartItem).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
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

    override fun removeCartItem(id: Int) {
        cartItemService.deleteCartItem(id).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
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

    override fun updateCartItem(quantity: Quantity) {
        TODO("Not yet implemented")
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