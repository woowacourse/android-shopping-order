package woowacourse.shopping.data.datasource.local

interface AuthInfoLocalDataSource {

    fun getAuthInfo(): String?
    fun setAuthInfo()
}
