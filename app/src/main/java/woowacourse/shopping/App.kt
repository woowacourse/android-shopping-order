package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.data.RepositoryContainer

class App : Application() {
    lateinit var repositoryContainer: RepositoryContainer

    override fun onCreate() {
        super.onCreate()
        repositoryContainer = RepositoryContainer(applicationContext)
    }
}
