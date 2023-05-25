package woowacourse.shopping.util.preference

interface BasePreference {
    fun getToken(): String?
    fun setToken(newToken: String)
    fun getBaseUrl(): String?
    fun setBaseUrl(newBaseUrl: String)
}
