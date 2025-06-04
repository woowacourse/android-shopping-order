package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.AuthStorage

object AuthStorageModule {
    private lateinit var appContext: Context
    private const val ERROR_APP_CONTEXT_NOT_INITIALIZE = "appContext가 초기화되지 않았습니다."

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val authStorage: AuthStorage by lazy {
        check(::appContext.isInitialized) { ERROR_APP_CONTEXT_NOT_INITIALIZE }
        AuthStorage(appContext)
    }
}
