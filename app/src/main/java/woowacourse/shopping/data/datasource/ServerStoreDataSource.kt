package woowacourse.shopping.data.datasource

interface ServerStoreDataSource {
    fun setServerUrl(url: String)
    fun getServerUrl(): String
}
