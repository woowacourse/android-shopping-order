package woowacourse.shopping.ui.application

import android.app.Application
import woowacourse.shopping.data.remote.NetworkModuleUsingSerialization

class OrderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initInterceptor()
    }

    private fun initInterceptor() {
        NetworkModuleUsingSerialization.setApplicationContext(this.applicationContext)
    }
}
