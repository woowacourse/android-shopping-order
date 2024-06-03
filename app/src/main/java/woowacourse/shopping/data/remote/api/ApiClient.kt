package woowacourse.shopping.data.remote.api

interface ApiClient {
    fun <T> createService(service: Class<T>): T
}
