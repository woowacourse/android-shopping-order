package woowacourse.shopping.data.payment.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.util.HeaderInterceptor
import woowacourse.shopping.data.util.RetrofitService

class OrderRemoteDataSourceImpl(
    baseUrl: String = BuildConfig.BASE_URL,
) : OrderRemoteDataSource {
    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor())
            .build()

    private val retrofitService: RetrofitService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RetrofitService::class.java)
}
