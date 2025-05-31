package woowacourse.shopping.data

import android.content.Context
import androidx.core.content.edit
import java.util.Base64

class TokenProvider(
    context: Context,
    name: String,
    password: String,
) {
    private val storage =
        context.getSharedPreferences(
            SHARED_NAME,
            Context.MODE_PRIVATE,
        )

    init {
        setToken(name, password)
    }

    fun getToken(): String? = storage.getString(KEY_TOKEN, null)

    private fun setToken(
        name: String,
        password: String,
    ) {
        storage.edit { putString(KEY_TOKEN, encodeToken(name, password)) }
    }

    private fun encodeToken(
        name: String,
        password: String,
    ): String = Base64.getEncoder().encodeToString("$name:$password".toByteArray())

    companion object {
        private const val SHARED_NAME = "Authorization"
        private const val KEY_TOKEN = "token"
    }
}
