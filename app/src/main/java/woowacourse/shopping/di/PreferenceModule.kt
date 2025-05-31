package woowacourse.shopping.di

import android.annotation.SuppressLint
import android.content.Context
import woowacourse.shopping.data.preference.AuthSharedPreference

@SuppressLint("StaticFieldLeak")
object PreferenceModule {
    private var _authSharedPreference: AuthSharedPreference? = null
    val authSharedPreference: AuthSharedPreference get() = _authSharedPreference ?: throw IllegalStateException()

    fun init(context: Context) {
        if (_authSharedPreference == null) {
            _authSharedPreference = AuthSharedPreference(context)
        }
    }
}
