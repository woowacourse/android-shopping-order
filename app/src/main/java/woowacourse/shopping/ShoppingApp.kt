package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.DatabaseModule
import woowacourse.shopping.di.PreferenceModule

class ShoppingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseModule.init(this)
        PreferenceModule.init(this)
    }
}
