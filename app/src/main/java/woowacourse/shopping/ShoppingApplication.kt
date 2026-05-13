package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.di.AppContainer

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }


    companion object {
        val baseUrl: String =
            "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"
    }
}
