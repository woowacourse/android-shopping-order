package woowacourse.shopping.data.remote.cart

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object CartClient {
    private val contentType = "application/json".toMediaType()

    private val okHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor(BuildConfig.USER_ID, BuildConfig.USER_PASSWORD))
            .build()

    private val _retrofitService: CartService by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(CartService::class.java)
    }

    fun getRetrofitService(): CartService = _retrofitService
}
