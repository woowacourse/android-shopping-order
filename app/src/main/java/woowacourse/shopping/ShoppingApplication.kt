package woowacourse.shopping

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShoppingApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)

        applicationScope.launch {
            val savedUsername = appContainer.userDataStore.username.first()
            val savedPassword = appContainer.userDataStore.password.first()

            if (savedUsername.isEmpty() || savedPassword.isEmpty()) {
                appContainer.userDataStore.saveUser(
                    username = BuildConfig.TEST_USERNAME,
                    password = BuildConfig.TEST_PASSWORD
                )
            }

            appContainer.authRepository.loadCredentialsToMemory()
        }
    }
}
