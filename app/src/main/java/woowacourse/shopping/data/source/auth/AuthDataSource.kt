package woowacourse.shopping.data.source.auth

interface AuthDataSource {
    suspend fun load(): String

    suspend fun save(token: String)
}
