package woowacourse.shopping.data.remote.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.BaseResponse
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.data.mapper.toDomain

class CartRemoteDataSource(private val sharedPreferences: SharedPreferencesDb) : CartDataSource {
    private val retrofitService = ApiClient.client
        .create(CartService::class.java)

    override fun addCartItem(productId: Int, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        retrofitService.addCartItem(AddCartRequestBody(productId))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    val locationHeader = response.headers()["Location"]
                    val cartItemId = extractCartItemIdFromLocation(locationHeader)
                    onSuccess(cartItemId ?: -1)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("HttpError", t.message.toString())
                    onFailure()
                }
            })
    }

    private fun extractCartItemIdFromLocation(locationHeader: String?): Int? {
        if (locationHeader.isNullOrEmpty()) return null
        val lastSlashIndex = locationHeader.lastIndexOf('/')
        return locationHeader.substring(lastSlashIndex + 1).toInt()
    }

    override fun deleteCartItem(cartItemId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        retrofitService.deleteCartItem(cartItemId)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("HttpError", t.message.toString())
                    onFailure()
                }
            })
    }

    override fun updateCartItemQuantity(
        cartId: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        return retrofitService.updateCartItemCount(
            cartItemId = cartId,
            quantityBody = UpdateQuantityRequestBody(count)
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("HttpError", t.message.toString())
                onFailure()
            }
        })
    }

    override fun getAllCartProductsInfo(
        onSuccess: (List<CartProductInfo>) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitService.getAllCartItems()
            .enqueue(object : Callback<BaseResponse<List<CartDataModel>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<CartDataModel>>>,
                    response: Response<BaseResponse<List<CartDataModel>>>,
                ) {
                    val cartItems = response.body()?.result?.map { it.toDomain() }
                    if (cartItems == null) {
                        onSuccess(emptyList())
                    } else {
                        onSuccess(cartItems)
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse<List<CartDataModel>>>,
                    t: Throwable
                ) {
                    Log.d("HttpError", t.message.toString())
                    onFailure()
                }
            })
    }
}
