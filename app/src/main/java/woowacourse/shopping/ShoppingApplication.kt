package woowacourse.shopping

import android.app.Application
import android.content.SharedPreferences
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.server.DevMockServer
import kotlin.concurrent.thread

class ShoppingApplication : Application() {
    lateinit var database: ShoppingDatabase
        private set

    val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(USER_INFO, MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()

        thread { DevMockServer.start() }
        database = ShoppingDatabase.getInstance(applicationContext)

        sharedPreferences.edit().putString(ID, "ijh1298")
        sharedPreferences.edit().putString(PASSWORD, "password")
    }

    companion object {
        private const val USER_INFO = "user_info"
        private const val ID = "id"
        private const val PASSWORD = "password"
    }
}
