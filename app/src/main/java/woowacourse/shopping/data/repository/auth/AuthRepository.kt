package woowacourse.shopping.data.repository.auth

interface AuthRepository {
    suspend fun load(): String

    suspend fun save(token: String)
}
