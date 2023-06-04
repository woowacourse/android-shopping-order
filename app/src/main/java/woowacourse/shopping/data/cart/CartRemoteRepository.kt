package woowacourse.shopping.data.cart

import com.example.domain.CartProduct
import com.example.domain.repository.CartRepository
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.cart.model.CartProductResponse
import woowacourse.shopping.data.cart.model.toDomain
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.util.BANDAL

class CartRemoteRepository(
    private val user: String = BANDAL,
    url: String,
    port: String = "8080",
) : CartRepository {
    private val authorizationHttpClient = OkHttpClient.Builder().apply {
        addInterceptor { chain ->
            val interceptedRequest = chain.request()
            val headerAddedRequest =
                interceptedRequest.newBuilder().header("Authorization", "Basic $user").build()
            chain.proceed(headerAddedRequest)
        }
    }.build()

    private val baseUrl = "$url:$port"
    private val retrofitCartService: RetrofitCartService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(authorizationHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitCartService::class.java)

    override fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit) {
        retrofitCartService.requestAllCartProducts()
            .enqueue(object : Callback<List<CartProductResponse>> {
                override fun onResponse(
                    call: Call<List<CartProductResponse>>,
                    response: retrofit2.Response<List<CartProductResponse>>
                ) {
                    val result = response.body() ?: emptyList()
                    if (400 <= response.code()) return onFailure()
                    onSuccess(result.map(CartProductResponse::toDomain))
                }

                override fun onFailure(
                    call: Call<List<CartProductResponse>>,
                    t: Throwable
                ) {
                    onFailure()
                }
            })
    }

    override fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitCartService.requestAddCartProduct(productId)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    val result: Long =
                        response.headers()["Location"]?.split("/")?.last()?.toLong() ?: 0L
                    if (400 <= response.code()) return onFailure()
                    onSuccess(result)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun updateCartProductQuantity(
        id: Long,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitCartService.requestUpdateQuantity(id, quantity)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun deleteCartProduct(id: Long, onSuccess: () -> Unit, onFailure: () -> Unit) {
        retrofitCartService.requestDeleteCartProduct(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
