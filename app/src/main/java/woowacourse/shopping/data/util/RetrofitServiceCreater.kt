package woowacourse.shopping.data.util

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.data.service.cart.CartService
import woowacourse.shopping.data.service.product.ProductService
import woowacourse.shopping.data.util.okhttp.Header
import woowacourse.shopping.data.util.okhttp.Header.AUTHORIZATION

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authRequest = chain.request().newBuilder()
            .addHeader(Header.of(AUTHORIZATION), "Basic ${pref.getToken()}")
            .build()
        return chain.proceed(authRequest)
    }
}

private fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()
}

private val retrofit = Retrofit.Builder()
    .baseUrl(pref.getBaseUrl() ?: "")
    .client(provideOkHttpClient(AuthInterceptor()))
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val productService = retrofit.create(ProductService::class.java)
val cartService = retrofit.create(CartService::class.java)
