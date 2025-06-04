package woowacourse.shopping.data.datasource

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
        callback: (Long) -> Unit,
    ) {
        cartItemService.postCartItem(cartItem).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val cartId = response.extractCartItemId()
                        println("body : $body")
                        cartId?.let { callback(it) }
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

    override fun removeCartItem(
        cartId: Long,
        callback: (Long) -> Unit,
    ) {
        cartItemService.deleteCartItem(cartId).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        callback(cartId)
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
        cartId: Long,
        quantity: Quantity,
        onResult: (Long) -> Unit,
    ) {
        cartItemService.patchCartItem(cartId, quantity).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        println("body : $body")
                        onResult(cartId)
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

    private fun Response<*>.extractCartItemId(): Long? {
        val locationHeader = this.headers()[HEADER_LOCATION]
        return locationHeader
            ?.substringAfter(HEADER_CART_ID_PREFIX)
            ?.takeWhile { it.isDigit() }
            ?.toLongOrNull()
    }

    companion object {
        private const val HEADER_LOCATION = "Location"
        private const val HEADER_CART_ID_PREFIX = "/cart-items/"
    }
}
