package woowacourse.shopping.domain.repository

interface AuthRepository {
    fun setToken(token: String)
    fun getToken(): String?
}
