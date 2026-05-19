package woowacourse.shopping.data.source.local.auth

interface AuthDataSource {
    suspend fun getToken(): String

    suspend fun saveToken(
        id: String,
        password: String,
    )
}
