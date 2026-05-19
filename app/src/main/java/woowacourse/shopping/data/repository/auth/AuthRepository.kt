package woowacourse.shopping.data.repository.auth

interface AuthRepository {
    suspend fun token(): String
}
