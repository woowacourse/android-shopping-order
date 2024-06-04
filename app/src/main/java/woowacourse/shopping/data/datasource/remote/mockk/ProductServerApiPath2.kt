package woowacourse.shopping.data.datasource.remote.mockk

object ProductServerApiPath2 {
    const val BASE_PORT = 8080
    private const val BASE_URL = "http://54.180.95.212"

    const val FIND_PRODUCT_PAGE_PATH = "/products"
    const val FIND_PRODUCT_PAGE_URL = "$BASE_URL$FIND_PRODUCT_PAGE_PATH?page=%d&size=%d"

    const val CONTENT_TYPE = "Content-Type"
    const val CONTENT_KEY = "application/json"
}
