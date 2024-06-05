package woowacourse.shopping.data.service

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import woowacourse.shopping.BuildConfig

object ShoppingOkHttpClient {
    val INSTANCE: OkHttpClient =
        OkHttpClient.Builder().addInterceptor(tokenInterceptor()).build()

    private fun tokenInterceptor(): Interceptor {
        val tokenInterceptor =
            Interceptor { chain ->
                var request = chain.request()
                request = request.newBuilder().header("Authorization", BuildConfig.TOKEN).build()
                chain.proceed(request)
            }
        return tokenInterceptor
    }
}
