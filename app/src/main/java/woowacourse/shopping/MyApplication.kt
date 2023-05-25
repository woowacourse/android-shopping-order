package woowacourse.shopping

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Storage.init(this)
    }
}
