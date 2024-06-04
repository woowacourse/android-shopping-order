package woowacourse.shopping.data.provider

abstract class AuthProvider {
    abstract var name: String

    abstract var password: String

    companion object {
        private var instance: AuthProvider? = null

        fun setInstance(authProvider: AuthProvider) {
            instance = authProvider
        }

        fun getInstance(): AuthProvider = requireNotNull(instance)
    }
}
