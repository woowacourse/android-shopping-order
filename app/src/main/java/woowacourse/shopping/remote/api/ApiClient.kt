package woowacourse.shopping.remote.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var apiClient: Retrofit? = null

    fun getApiClient(): Retrofit = requireNotNull(apiClient)

    fun setApiClient(
        baseUrl: String,
        authorizationInterceptor: Interceptor,
    ) {
        apiClient =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(provideOkHttpClient(authorizationInterceptor))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }
}
