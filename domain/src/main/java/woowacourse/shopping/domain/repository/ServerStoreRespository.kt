package woowacourse.shopping.domain.repository

interface ServerStoreRespository {
    fun setServerUrl(url: String)
    fun getServerUrl(): String
}
