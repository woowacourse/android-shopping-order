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
        val baseUrl = BuildConfig.base_url
        val userName = BuildConfig.authorization_username
        val password = BuildConfig.authorization_password
        val interceptor = AuthorizationInterceptor(userName, password)

        ApiClient.setApiClient(baseUrl, interceptor)
    }
}
