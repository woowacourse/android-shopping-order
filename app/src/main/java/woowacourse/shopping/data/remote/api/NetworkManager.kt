package woowacourse.shopping.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig

object NetworkManager {
    private var instance: Retrofit? = null
    const val ACCEPT_HEADER = "*/*"
    const val ACCEPT_KEY = "accept"
    const val AUTH_KEY = "Authorization"

    private fun getRetrofit(): Retrofit {
        return instance ?: Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(BasicAuthInterceptor(BuildConfig.USER_ID, BuildConfig.USER_PW))
                    .build(),
            )
            .build()
    }

    fun productService(): ProductApiService = getRetrofit().create(ProductApiService::class.java)

    fun cartService(): CartApiService = getRetrofit().create(CartApiService::class.java)

    fun orderService(): OrderApiService = getRetrofit().create(OrderApiService::class.java)
}
