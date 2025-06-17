package woowacourse.shopping.domain.model

import java.util.Base64

object Authorization {
    var isLogin: Boolean = false
        private set

    var basicKey: String = ""
        private set

    fun setLoginStatus(status: Boolean) {
        isLogin = status
    }

    fun setBasicKeyByIdPw(
        id: String,
        pw: String,
    ) {
        val bytes = ("$id:$pw").toByteArray(Charsets.UTF_8)
        setBasicKey(Base64.getEncoder().encodeToString(bytes))
    }

    fun setBasicKey(basicKey: String) {
        this.basicKey = basicKey
    }
}
