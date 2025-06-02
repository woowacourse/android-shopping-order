package woowacourse.shopping.data.token

interface TokenProvider {
    fun getToken(): String?

    fun setToken(
        name: String,
        password: String,
    )
}
