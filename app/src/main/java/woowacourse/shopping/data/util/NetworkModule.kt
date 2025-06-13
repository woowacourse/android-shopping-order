package woowacourse.shopping.data.util

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object NetworkModule {
    private val okHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor())
            .build()
    }

    val retrofitService: RetrofitService by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RetrofitService::class.java)
    }
}
