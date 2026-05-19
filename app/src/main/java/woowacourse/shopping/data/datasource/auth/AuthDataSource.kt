package woowacourse.shopping.data.datasource.auth

interface AuthDataSource {
    suspend fun load(): String

    suspend fun save(token: String)
}
