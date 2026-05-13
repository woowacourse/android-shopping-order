package woowacourse.shopping.domain.repository

import android.util.Base64

class AuthRepository {
    val id = "joon0447"
    val password = "password"

    val authString = "$id:$password"
    val getAuthToken = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
}
