package woowacourse.shopping.data.repository

interface ServerStoreRespository {
    fun setServerUrl(url: String)
    fun getServerUrl(): String
}
