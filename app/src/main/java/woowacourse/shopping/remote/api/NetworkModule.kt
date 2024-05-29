package woowacourse.shopping.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.app.ShoppingApplication.Companion.BASE_URL
import woowacourse.shopping.remote.interceptor.AuthorizationInterceptor
import woowacourse.shopping.remote.interceptor.HttpExceptionInterceptor

object NetworkModule {
    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthorizationInterceptor())
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
}
