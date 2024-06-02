package woowacourse.shopping.data.remote.api

interface CredentialsProvider {
    fun getUsername(): String
    fun getPassword(): String
}
