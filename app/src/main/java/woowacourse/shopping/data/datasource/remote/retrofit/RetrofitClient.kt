package woowacourse.shopping.data.datasource.remote.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit

class RetrofitClient private constructor(
    baseUrl: String,
    authToken: String,
) {

    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient(AppInterceptor(authToken)))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val productDataService: ProductDataService by lazy {
        retrofit.create(ProductDataService::class.java)
    }

    val shoppingCartService: ShoppingCartService by lazy {
        authRetrofit.create(ShoppingCartService::class.java)
    }

    val couponDataService: CouponDataService by lazy {
        authRetrofit.create(CouponDataService::class.java)
    }

    val orderDataService: OrderDataService by lazy {
        authRetrofit.create(OrderDataService::class.java)
    }

    private fun provideOkHttpClient(
        interceptor: AppInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor(private val authToken: String) :
        Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", authToken)
                .build()
            proceed(newRequest)
        }
    }

    companion object {
        private var retrofitClient: RetrofitClient? = null

        fun getInstance(
            baseUrl: String = "",
            authToken: String = "",
        ): RetrofitClient {
            return retrofitClient ?: synchronized(this) {
                RetrofitClient(baseUrl, authToken).also {
                    retrofitClient = it
                }
            }
        }
    }
}
