package woowacourse.shopping.data.authentication.repository

import woowacourse.shopping.data.authentication.model.UserAuthentication

interface AuthenticationRepository {
    val id: String
    val password: String

    fun updateUserAuthentication(userAuthentication: UserAuthentication)
}
