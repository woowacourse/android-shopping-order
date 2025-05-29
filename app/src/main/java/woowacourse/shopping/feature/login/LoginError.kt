package woowacourse.shopping.feature.login

sealed class LoginError {
    object NotFound : LoginError()

    object Network : LoginError()
}
