package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.datasource.local.auth.TokenSharedPreference

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        tokenPreference = TokenSharedPreference.getInstance(this)
    }

    companion object {
        lateinit var tokenPreference: TokenSharedPreference
    }
}
