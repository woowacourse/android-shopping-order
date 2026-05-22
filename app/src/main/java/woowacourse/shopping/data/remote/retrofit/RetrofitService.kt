package woowacourse.shopping.data.remote.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.AcceptHeaderInterceptor
import woowacourse.shopping.data.remote.AuthHeaderProvider
import woowacourse.shopping.data.remote.AuthInterceptor
import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.api.ShoppingCartRetrofitInterface

class RetrofitService(
    authHeaderProvider: AuthHeaderProvider,
) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(AcceptHeaderInterceptor())
            .addInterceptor(AuthInterceptor(authHeaderProvider))
            .build()

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    val orderApiService: OrderRetrofitInterface =
        retrofit.create(OrderRetrofitInterface::class.java)
    val productApiService: ProductRetrofitInterface =
        retrofit.create(ProductRetrofitInterface::class.java)
    val shoppingCartApiService: ShoppingCartRetrofitInterface =
        retrofit.create(ShoppingCartRetrofitInterface::class.java)
}
