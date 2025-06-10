package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.datasource.local.UserPreference
import woowacourse.shopping.di.DatabaseModule

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseModule.init(this)
        UserPreference.init(this)
    }
}
