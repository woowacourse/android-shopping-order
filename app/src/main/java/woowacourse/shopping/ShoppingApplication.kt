package woowacourse.shopping

import android.app.Application
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ShoppingApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)

        runBlocking {
            val savedUsername = appContainer.userDataStore.userCredentialsFlow.first()

            if (savedUsername.username.isBlank()) {
                appContainer.userDataStore.saveUser(
                    username = BuildConfig.TEST_USERNAME,
                    password = BuildConfig.TEST_PASSWORD,
                )
            }
            appContainer.authRepository.loadCredentialsToMemory()
        }
    }
}
