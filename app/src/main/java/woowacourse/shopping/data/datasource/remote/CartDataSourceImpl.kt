package woowacourse.shopping.data.datasource.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.cart.CartItemCountResponse
import woowacourse.shopping.data.dto.cart.CartItemRequest
import woowacourse.shopping.data.dto.cart.CartsResponse
import woowacourse.shopping.data.dto.cart.UpdateCartRequest
import woowacourse.shopping.data.dto.cart.toDomain
import woowacourse.shopping.data.remote.CartItemService
import woowacourse.shopping.domain.model.CartItem

class CartDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartDataSource {
    override fun getTotalCount(onResult: (Result<Int>) -> Unit) =
        cartItemService.requestCartItemCount().enqueue(
            object : Callback<CartItemCountResponse> {
                override fun onResponse(
                    call: Call<CartItemCountResponse>,
                    response: Response<CartItemCountResponse>,
                ) {
                    if (response.isSuccessful) {
                        onResult(Result.success(response.body()?.quantity ?: 0))
                    }
                }

                override fun onFailure(
                    call: Call<CartItemCountResponse>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )

    override fun getPagedCartItems(
        page: Int,
        size: Int?,
        onResult: (List<CartItem>) -> Unit,
    ) = cartItemService.requestCartItems(page = page, size = size).enqueue(
        object : Callback<CartsResponse> {
            override fun onResponse(
                call: Call<CartsResponse>,
                response: Response<CartsResponse>,
            ) {
                if (response.isSuccessful) {
                    val body =
                        response.body()?.cartContent?.map { it.toDomain() }
                            ?: emptyList()
                    onResult(body)
                }
            }

            override fun onFailure(
                call: Call<CartsResponse>,
                t: Throwable,
            ) {
                onResult(emptyList())
            }
        },
    )

    override fun insertCartItem(
        productId: Long,
        quantity: Int,
        onResult: (Result<Long>) -> Unit,
    ) {
        val request = CartItemRequest(productId, quantity)
        cartItemService.addCartItem(request).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val cartId = response.toIdOrNull() ?: throw IllegalStateException("")
                        onResult(Result.success(cartId))
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    override fun updateQuantity(
        cartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val request = UpdateCartRequest(quantity)
        cartItemService.updateCartItem(cartId, request).enqueue(
            object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        onResult(Result.success(Unit))
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    override fun deleteCartItemById(
        cartId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = cartItemService.deleteCartItem(cartId).enqueue(
        object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>,
            ) {
                if (response.isSuccessful) {
                    onResult(Result.success(Unit))
                }
            }

            override fun onFailure(
                call: Call<ResponseBody>,
                t: Throwable,
            ) {
                onResult(Result.failure(t))
            }
        },
    )

    private fun <T> Response<T>.toIdOrNull(): Long? =
        headers()["LOCATION"]?.substringAfterLast("/")?.toLongOrNull()
}
