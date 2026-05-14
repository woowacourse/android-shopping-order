package woowacourse.shopping.backend.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.backend.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.backend.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.backend.retrofit.api.ShoppingCartRetrofitInterface
import woowacourse.shopping.repository.AuthHeaderProvider
import woowacourse.shopping.repository.AuthInterceptor

class RetrofitService(
    authHeaderProvider: AuthHeaderProvider,
) {
    private val client =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authHeaderProvider))
            .build()

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val orderApiService: OrderRetrofitInterface =
        retrofit.create(OrderRetrofitInterface::class.java)
    val productApiService: ProductRetrofitInterface =
        retrofit.create(ProductRetrofitInterface::class.java)
    val shoppingCartApiService: ShoppingCartRetrofitInterface =
        retrofit.create(ShoppingCartRetrofitInterface::class.java)

    companion object {
        private const val BASE_URL =
            "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"
    }
}
