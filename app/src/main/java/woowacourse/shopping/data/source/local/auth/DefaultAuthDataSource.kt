package woowacourse.shopping.data.source.local.auth

import android.util.Base64

object DefaultAuthDataSource : AuthDataSource {
    val id = "joon0447"
    val password = "password"

    val authString = "$id:$password"

    override val getAuthToken = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
}
