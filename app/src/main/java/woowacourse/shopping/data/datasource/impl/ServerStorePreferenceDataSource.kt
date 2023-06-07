package woowacourse.shopping.data.datasource.impl

import android.content.Context
import androidx.preference.PreferenceManager
import woowacourse.shopping.data.datasource.ServerStoreDataSource

class ServerStorePreferenceDataSource(context: Context) : ServerStoreDataSource {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun setServerUrl(url: String) {
        val editor = sharedPreferences.edit()
        editor.putString(SERVER, url).apply()
    }

    override fun getServerUrl(): String = sharedPreferences.getString(SERVER, "") ?: ""

    companion object {
        const val SERVER = "SERVER"
    }
}
