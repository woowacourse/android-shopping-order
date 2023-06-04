package woowacourse.shopping.data.cart

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.cart.dto.CartProduct
import woowacourse.shopping.data.cart.dto.ProductInsertCartRequest

class CartRemoteDataSource(baseUrl: String, private val userId: String) : CartDataSource {

    private val retrofitService: CartRetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CartRetrofitService::class.java)

    override fun insertCartProduct(
        productId: Long,
        quantity: Int,
        callback: (cartId: Long) -> Unit,
    ) {
        retrofitService.insertCartProduct(userId, ProductInsertCartRequest(productId, quantity))
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        val location = response.headers()[LOCATION]?.split("/")?.last()
                        callback(location?.toLong() ?: 0)
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    override fun updateCartProduct(cartId: Long, quantity: Int, callback: () -> Unit) {
        retrofitService.updateCartProduct(userId, cartId, quantity)
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        callback()
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    override fun deleteCartProduct(cartId: Long, callback: () -> Unit) {
        retrofitService.deleteCartProduct(userId, cartId)
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        callback()
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    override fun getAllCartProducts(callback: (List<CartProduct>) -> Unit) {
        retrofitService.requestCartProducts(userId)
            .enqueue(
                object : retrofit2.Callback<List<CartProduct>> {
                    override fun onResponse(
                        call: Call<List<CartProduct>>,
                        response: Response<List<CartProduct>>,
                    ) {
                        val cartProducts = response.body() ?: listOf()
                        callback(cartProducts)
                    }

                    override fun onFailure(call: Call<List<CartProduct>>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    companion object {
        private const val LOCATION = "Location"
    }
}
