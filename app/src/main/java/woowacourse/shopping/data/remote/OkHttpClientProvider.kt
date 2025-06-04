package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import woowacourse.shopping.di.InterceptorModule

object OkHttpClientProvider {
    fun provideClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(InterceptorModule.provideLoggingInterceptor())
            .addInterceptor(InterceptorModule.provideAuthorizationInterceptor())
            .build()
}
