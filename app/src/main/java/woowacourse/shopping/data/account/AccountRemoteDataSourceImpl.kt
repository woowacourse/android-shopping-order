package woowacourse.shopping.data.account

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.util.HeaderInterceptor
import woowacourse.shopping.data.util.RetrofitService

class AccountRemoteDataSourceImpl(
    baseUrl: String = BuildConfig.BASE_URL,
) : AccountRemoteDataSource {
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

    override suspend fun fetchAuthCode(validKey: String): CartFetchResult<Int> {
        try {
            val response = retrofitService.requestCartCounts()
            return when {
                response.isSuccessful -> CartFetchResult.Success(response.code())
                else -> CartFetchResult.Error(CartFetchError.Server(response.code(), response.message()))
            }
        } catch (e: Exception) {
            return CartFetchResult.Error(CartFetchError.Network)
        }
    }
}
