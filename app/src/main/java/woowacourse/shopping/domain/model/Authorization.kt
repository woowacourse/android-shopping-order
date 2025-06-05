package woowacourse.shopping.domain.model

import java.util.Base64

object Authorization {
    private var _isLogin: Boolean = false
    val isLogin get() = _isLogin
    private var _basicKey: String = ""
    val basicKey get() = _basicKey

    fun setLoginStatus(status: Boolean) {
        _isLogin = status
    }

    fun setBasicKey(basicKey: String) {
        _basicKey = basicKey
    }

    fun getBasicKey(
        id: String,
        pw: String,
    ): String {
        val bytes = ("$id:$pw").toByteArray(Charsets.UTF_8)
        return Base64.getEncoder().encodeToString(bytes)
    }
}
