package woowacourse.shopping.data.repository.auth

import woowacourse.shopping.data.datasource.auth.AuthDataSource

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource,
    private val issueToken: () -> String?,
) : AuthRepository {
    override suspend fun token(): String {
        val stored = dataSource.load()
        if (stored.isNotBlank()) return stored

        val issued = issueToken().orEmpty()
        if (issued.isNotBlank()) dataSource.save(issued)
        return issued
    }
}
