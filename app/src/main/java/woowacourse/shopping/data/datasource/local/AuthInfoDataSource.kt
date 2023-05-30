package woowacourse.shopping.data.datasource.local

interface AuthInfoDataSource {

    fun getAuthInfo(): String?
    fun setAuthInfo(token: String)
}
