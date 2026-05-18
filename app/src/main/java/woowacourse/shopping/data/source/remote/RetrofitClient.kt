package woowacourse.shopping.data.source.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.source.local.auth.AuthDataSource
import woowacourse.shopping.data.source.remote.interceptor.AuthInterceptor

class RetrofitClient(
    authDataSource: AuthDataSource,
    baseUrl: String = RemoteConfig.BASE_URL,
) {
    private val okHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor(authDataSource))
            .build()

    private val json = Json { ignoreUnknownKeys = true }

    val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
