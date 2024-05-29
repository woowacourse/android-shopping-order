package woowacourse.shopping.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.app.ShoppingApplication.Companion.BASE_URL
import woowacourse.shopping.data.provider.AuthProvider
import woowacourse.shopping.remote.interceptor.AuthorizationInterceptor
import woowacourse.shopping.remote.interceptor.HttpExceptionInterceptor

class NetworkModule(authProvider: AuthProvider) {
    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthorizationInterceptor(authProvider = authProvider))
            .addInterceptor(HttpExceptionInterceptor())
            .build()

    val productService: ProductService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ProductService::class.java)

    val cartService: CartService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CartService::class.java)

    val orderService: OrderService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(OrderService::class.java)
}
