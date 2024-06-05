package woowacourse.shopping.data.remote

import okhttp3.Credentials
import okhttp3.Interceptor
import woowacourse.shopping.BuildConfig

object AuthInterceptor {
    val basicAuth =
        Interceptor { chain ->
            val user = BuildConfig.USER
            val password = BuildConfig.PASSWORD
            val credentials = Credentials.basic(user, password)
            val request =
                chain.request().newBuilder()
                    .header("Authorization", credentials)
                    .build()
            chain.proceed(request)
        }
}
