package woowacourse.shopping.data.respository.cart.source.remote

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.dto.response.CartResponse
import woowacourse.shopping.data.respository.cart.source.service.CartService
import woowacouse.shopping.model.cart.CartProduct

class CartRemoteDataSourceImpl(
    private val cartService: CartService,
) : CartRemoteDataSource {
    override fun requestDatas(
        onFailure: (message: String) -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    ) {
        cartService.requestDatas().enqueue(object : retrofit2.Callback<List<CartResponse>> {
            override fun onResponse(
                call: Call<List<CartResponse>>,
                response: Response<List<CartResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { carts ->
                        onSuccess(carts.map { it.toModel() })
                    } ?: response.errorBody()?.let { onFailure(it.string()) }
                    return
                }
                response.errorBody()?.let { onFailure(it.string()) }
            }

            override fun onFailure(call: Call<List<CartResponse>>, t: Throwable) {
                Log.e("Request Failed", t.toString())
                onFailure(ERROR_CONNECT)
            }
        })
    }

    override fun requestPatchCartItem(
        cartProduct: CartProduct,
        onFailure: (message: String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        cartService.requestPatchCartItem(cartProduct.id, cartProduct.count)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200) {
                        onSuccess()
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                    onFailure(ERROR_CONNECT)
                }
            })
    }

    override fun requestPostCartItem(
        productId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (Long) -> Unit,
    ) {
        cartService.requestPostCartItem(productId)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        val location = response.headers()["Location"]
                            ?: return response.errorBody()?.let { onFailure(it.string()) } ?: Unit

                        val cartId = location.substringAfterLast("cart-items/").toLong()
                        onSuccess(cartId)
                        return
                    }
                    response.errorBody()?.let { onFailure(it.string()) }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                    onFailure(ERROR_CONNECT)
                }
            })
    }

    override fun requestDeleteCartItem(cartId: Long) {
        val thread = Thread {
            cartService.requestDeleteCartItem(cartId).execute()
        }
        thread.start()
        thread.join()
    }

    companion object {
        private const val ERROR_CONNECT = "연결에 실패하였습니다. 다시 시도해주세요"
    }
}
