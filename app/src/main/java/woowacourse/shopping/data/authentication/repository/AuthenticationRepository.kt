package woowacourse.shopping.data.authentication.repository

import woowacourse.shopping.domain.authentication.UserAuthentication

interface AuthenticationRepository {
    val id: String
    val password: String

    suspend fun updateUserAuthentication(userAuthentication: UserAuthentication)
}
