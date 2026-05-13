package woowacourse.shopping.network.auth

import woowacourse.shopping.BuildConfig

data class AuthCredentials(
    val userId: String,
    val password: String,
)

object AppAuthConfig {
    val credentials: AuthCredentials by lazy {
        AuthCredentials(
            userId = BuildConfig.AUTH_USER_ID,
            password = BuildConfig.AUTH_PASSWORD,
        )
    }
}
