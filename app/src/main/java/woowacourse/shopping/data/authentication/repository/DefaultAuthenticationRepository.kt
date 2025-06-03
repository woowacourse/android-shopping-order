package woowacourse.shopping.data.authentication.repository

import woowacourse.shopping.data.authentication.dataSource.AuthenticationLocalDataSource
import woowacourse.shopping.data.authentication.model.UserAuthentication

class DefaultAuthenticationRepository(
    val authenticationLocalDataSource: AuthenticationLocalDataSource,
) : AuthenticationRepository {
    override val id: String
        get() = authenticationLocalDataSource.id
    override val password: String
        get() = authenticationLocalDataSource.password

    override fun updateUserAuthentication(userAuthentication: UserAuthentication) {
        authenticationLocalDataSource.updateId(userAuthentication.id)
        authenticationLocalDataSource.updatePassword(userAuthentication.password)
    }

    companion object {
        private var instance: AuthenticationRepository? = null

        fun initialize(authenticationLocalDataSource: AuthenticationLocalDataSource) {
            if (instance == null) {
                instance =
                    DefaultAuthenticationRepository(authenticationLocalDataSource = authenticationLocalDataSource)
            }
        }

        fun get(): AuthenticationRepository = instance ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
