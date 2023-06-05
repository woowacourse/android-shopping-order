package woowacourse.shopping.data

import android.util.Base64

object UserAuthorization {
    private const val userEmail = "dooly@dooly.com"
    private const val userPassword = "1234"
    fun create(): String {
        val credentials = "$userEmail:$userPassword"
        return Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }
}
