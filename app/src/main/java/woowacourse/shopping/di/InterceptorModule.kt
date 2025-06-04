package woowacourse.shopping.di

import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import woowacourse.shopping.data.remote.interceptor.AuthorizationInterceptor
import woowacourse.shopping.data.remote.interceptor.DefaultLogger

object InterceptorModule {
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor(DefaultLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    fun provideAuthorizationInterceptor(): Interceptor = AuthorizationInterceptor()
}
