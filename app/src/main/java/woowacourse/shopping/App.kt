package woowacourse.shopping

import android.app.Application

class App : Application() {
    lateinit var repositoryContainer: RepositoryContainer

    override fun onCreate() {
        super.onCreate()
        repositoryContainer = RepositoryContainer(applicationContext)
    }
}
