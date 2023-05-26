package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.util.preference.BasePreference
import woowacourse.shopping.util.preference.ShoppingPreference

class ShoppingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        pref = ShoppingPreference(this)
    }

    companion object {
        lateinit var pref: BasePreference
    }
}
