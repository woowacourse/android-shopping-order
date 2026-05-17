package woowacourse.shopping.data.remote.auth

import woowacourse.shopping.data.repository.inmemory.InMemoryUserRepository
import kotlin.io.encoding.Base64

object BasicAuthEncoder {
    private val user = InMemoryUserRepository.STARTER

    fun getHeader() = encodeToBase64()

    private fun encodeToBase64(): String {
        val content = "${user.email}:${user.password}"
        val credentials = Base64.encode(content.toByteArray(), 0)
        return "Basic $credentials"
    }
}
