package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.datasource.local.UserPreference
import woowacourse.shopping.di.DataSourceModule
import woowacourse.shopping.di.DatabaseModule
import woowacourse.shopping.di.RepositoryModule

class ShoppingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseModule.init(this)
        DataSourceModule.init(this)
        RepositoryModule.init(this)
        UserPreference.init(this)
    }
}
