package woowacourse.shopping.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.provider.AuthProvider
import woowacourse.shopping.remote.interceptor.AuthorizationInterceptor
import woowacourse.shopping.remote.interceptor.HttpExceptionInterceptor

class NetworkModule(authProvider: AuthProvider, baseUrl: String) {
    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthorizationInterceptor(authProvider = authProvider))
            .addInterceptor(HttpExceptionInterceptor())
            .build()

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    val productService: ProductService = retrofit.create(ProductService::class.java)

    val cartService: CartService = retrofit.create(CartService::class.java)

    val orderService: OrderService = retrofit.create(OrderService::class.java)
}
