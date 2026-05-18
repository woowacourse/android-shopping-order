package woowacourse.shopping.network.auth

import woowacourse.shopping.local.config.AppConfig
import java.nio.charset.StandardCharsets
import java.util.Base64

object BasicAuthHeaderFactory {
    fun create(): String {
        val source = "${AppConfig.USER_ID}:${AppConfig.PASSWORD}"
        val encoded = Base64.getEncoder().encodeToString(source.toByteArray(StandardCharsets.UTF_8))
        return "Basic $encoded"
    }
}
