package woowacourse.shopping.data.datasource.local

interface AuthInfoDataSource {

    fun getAuthInfo(token: String)
    fun setAuthInfo(token: String)
}
