package woowacourse.shopping.domain.model

import java.util.Base64

object Key {
    var id: String = ""
    var pw: String = ""
    val basicKey
        get() : String {
            val bytes = (id + ":" + pw).toByteArray(Charsets.UTF_8)
            return Base64.getEncoder().encodeToString(bytes)
        }

}