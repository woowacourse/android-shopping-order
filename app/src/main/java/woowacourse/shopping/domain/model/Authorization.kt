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

    fun setBasicKeyByIdPw(
        id: String,
        pw: String,
    ) {
        val bytes = ("$id:$pw").toByteArray(Charsets.UTF_8)
        setBasicKey(Base64.getEncoder().encodeToString(bytes))
    }

    fun setBasicKey(basicKey: String) {
        _basicKey = basicKey
    }
}
