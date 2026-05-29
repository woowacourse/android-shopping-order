package woowacourse.shopping.di

import android.content.Context
import okhttp3.OkHttpClient
import woowacourse.shopping.data.remote.auth.AppAuthConfig
import woowacourse.shopping.data.remote.auth.BasicAuthHeaderFactory
import woowacourse.shopping.data.remote.auth.BasicAuthInterceptor
import woowacourse.shopping.data.remote.common.ConnectivityManagerNetworkMonitor
import woowacourse.shopping.data.remote.common.NetworkMonitor

object NetworkProvider {
    const val PRODUCT_API_BASE_URL =
        "http://techcourse-lv2-alb-250216202.ap-northeast-2.elb.amazonaws.com/"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(
                BasicAuthInterceptor {
                    BasicAuthHeaderFactory.create(AppAuthConfig.credentials)
                },
            ).build()
    }

    fun provideHttpClient(): OkHttpClient = httpClient

    fun provideNetworkMonitor(context: Context): NetworkMonitor = ConnectivityManagerNetworkMonitor(context)
}
