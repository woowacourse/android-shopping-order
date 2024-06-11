package woowacourse.shopping.app

import android.app.Application
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.local.database.AppDatabase
import woowacourse.shopping.remote.api.ApiClient
import woowacourse.shopping.remote.interceptor.AuthorizationInterceptor

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
        initAuthorization()
    }

    private fun initAuthorization() {
        val baseUrl = BuildConfig.BASE_URL
        val userName = BuildConfig.USERNAME
        val password = BuildConfig.PASSWORD
        val interceptor = AuthorizationInterceptor(userName, password)

        ApiClient.setApiClient(baseUrl, interceptor)
    }
}
