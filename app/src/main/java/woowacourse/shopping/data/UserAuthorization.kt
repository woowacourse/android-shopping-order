package woowacourse.shopping.data

import android.util.Base64

object UserAuthorization {
    fun create(userEmail: String, userPassword: String): String {
        val credentials = "$userEmail:$userPassword"
        return Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }
}
