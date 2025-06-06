package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.DatabaseInjection
import woowacourse.shopping.di.PreferenceInjection

class ShoppingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseInjection.init(this)
        PreferenceInjection.init(this)
    }
}
