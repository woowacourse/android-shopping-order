package woowacourse.shopping.data.respository.cart.source.remote

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.cart.source.service.CartService
import woowacouse.shopping.model.cart.CartProduct

class CartRemoteDataSourceImpl(
    url: Server.Url,
    token: Server.Token,
) : CartRemoteDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl(url.value)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CartService::class.java)

    private val token = "Basic ${token.value}"

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    ) {
        retrofit.requestDatas(token).enqueue(object : retrofit2.Callback<List<CartRemoteEntity>> {
            override fun onResponse(
                call: Call<List<CartRemoteEntity>>,
                response: Response<List<CartRemoteEntity>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { carts ->
                        onSuccess(carts.map { it.toModel() })
                    } ?: onFailure()
                    return
                }
                onFailure()
            }

            override fun onFailure(call: Call<List<CartRemoteEntity>>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        })
    }

    override fun requestPatchCartItem(
        cartProduct: CartProduct,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        retrofit.requestPatchCartItem(token, cartProduct.id, cartProduct.count)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200) {
                        onSuccess()
                        return
                    }
                    onFailure()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                }
            })
    }

    override fun requestPostCartItem(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    ) {
        retrofit.requestPostCartItem(token, productId)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        val location = response.headers()["Location"] ?: return onFailure()
                        val cartId = location.substringAfterLast("cart-items/").toLong()
                        onSuccess(cartId)
                        return
                    }
                    onFailure()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("Request Failed", t.toString())
                }
            })
    }

    override fun requestDeleteCartItem(cartId: Long) {
        val thread = Thread {
            retrofit.requestDeleteCartItem(token, cartId).execute()
        }
        thread.start()
        thread.join()
    }
}
