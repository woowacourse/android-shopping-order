package woowacourse.shopping.data.service

import android.util.Base64
import okhttp3.OkHttpClient
import woowacourse.shopping.data.datasource.local.UserPreference

object OkHttpClientProvider {
    private val id = UserPreference.getUserInfo("id")
    private val password = UserPreference.getUserInfo("password")
    private val credentials = "$id:$password"
    private val basicAuth =
        "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

    fun provideClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(LoggingInterceptorProvider.provide())
            .addInterceptor(AuthorizationInterceptor(basicAuth))
            .build()
}
