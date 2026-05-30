package woowacourse.shopping.data.remote.auth

import java.nio.charset.StandardCharsets
import java.util.Base64

object BasicAuthHeaderFactory {
    fun create(credentials: AuthCredentials): String {
        val source = "${credentials.userId}:${credentials.password}"
        val encoded = Base64.getEncoder().encodeToString(source.toByteArray(StandardCharsets.UTF_8))
        return "Basic $encoded"
    }
}
