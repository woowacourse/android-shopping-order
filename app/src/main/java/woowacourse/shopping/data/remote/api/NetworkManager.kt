package woowacourse.shopping.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private var instance: Retrofit? = null
    private const val BASE_URL = "http://54.180.95.212:8080"

    private fun getRetrofit(): Retrofit {
        return instance ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(BasicAuthInterceptor("jinuemong","password"))
                    .build()
            )
            .build()
    }

    fun productService(): ProductApiService = getRetrofit().create(ProductApiService::class.java)
    fun cartService(): CartApiService = getRetrofit().create(CartApiService::class.java)
    fun orderService(): OrderApiService = getRetrofit().create(OrderApiService::class.java)
}
