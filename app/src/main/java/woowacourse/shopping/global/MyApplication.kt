package woowacourse.shopping.global

import android.app.Application
import woowacourse.shopping.data.preferences.UserPreference
import woowacourse.shopping.module.ApiModule

class MyApplication : Application() {
    override fun onCreate() {
        UserPreference.init(this)
        ApiModule.userStore = UserPreference
        super.onCreate()
    }
}
