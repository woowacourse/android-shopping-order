package woowacourse.shopping.data.remote.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.api.ShoppingCartRetrofitInterface
import woowacourse.shopping.data.remote.AuthHeaderProvider
import woowacourse.shopping.data.remote.AuthInterceptor

class RetrofitService(
    authHeaderProvider: AuthHeaderProvider,
) {
    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor(authHeaderProvider))
            .build()

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val orderApiService: OrderRetrofitInterface =
        retrofit.create(OrderRetrofitInterface::class.java)
    val productApiService: ProductRetrofitInterface =
        retrofit.create(ProductRetrofitInterface::class.java)
    val shoppingCartApiService: ShoppingCartRetrofitInterface =
        retrofit.create(ShoppingCartRetrofitInterface::class.java)

}
