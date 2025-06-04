package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.dao.RecentlyProductDao
import woowacourse.shopping.data.database.CartDatabase

object DatabaseModule {
    private lateinit var appContext: Context
    private const val ERROR_APP_CONTEXT_NOT_INITIALIZE = "appContext가 초기화되지 않았습니다."

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val database: CartDatabase by lazy {
        check(::appContext.isInitialized) { ERROR_APP_CONTEXT_NOT_INITIALIZE }
        CartDatabase.getInstance(appContext)
    }

    val recentProductDao: RecentlyProductDao by lazy {
        database.recentlyProductDao()
    }
}
