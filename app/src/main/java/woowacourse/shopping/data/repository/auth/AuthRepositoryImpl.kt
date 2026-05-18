package woowacourse.shopping.data.repository.auth

import woowacourse.shopping.data.source.auth.AuthDataSource

class AuthRepositoryImpl(
    val dataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun load(): String = dataSource.load()

    override suspend fun save(token: String) {
        dataSource.save(token)
    }
}
