package woowacourse.shopping.data.authentication.dataSource

interface AuthenticationLocalDataSource {
    val id: String
    val password: String

    fun updateId(id: String)

    fun updatePassword(password: String)
}
