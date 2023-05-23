package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.util.preference.ShoppingPreference

class ShoppingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        pref = ShoppingPreference(this)
        pref.setToken("dG1kZ2gxNTkyQG5hdmVyLmNvbToxMjM0")
    }

    companion object {
        lateinit var pref: ShoppingPreference
    }
}
