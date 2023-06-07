package woowacourse.shopping.global

import android.app.Application
import woowacourse.shopping.data.preferences.UserPreference

class MyApplication : Application() {
    override fun onCreate() {
        UserPreference.init(this)
        super.onCreate()
    }
}
