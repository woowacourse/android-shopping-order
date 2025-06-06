package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.product.database.RecentViewedProductDatabase

object DatabaseModule {
    private lateinit var appContext: Context
    private const val ERROR_APP_CONTEXT_NOT_INITIALIZE = "appContext가 초기화되지 않았습니다."

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val recentViewedProductDatabase: RecentViewedProductDatabase by lazy {
        check(::appContext.isInitialized) { ERROR_APP_CONTEXT_NOT_INITIALIZE }
        RecentViewedProductDatabase.getDataBase(appContext)
    }

    val recentViewedProductDao by lazy { recentViewedProductDatabase.dao() }
}
