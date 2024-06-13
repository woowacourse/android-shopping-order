package woowacourse.shopping.data.remote.api

object ProductMockWebServerPath {
    const val BASE_PORT = 12345
    private const val BASE_URL = "http://localhost:$BASE_PORT"

    private const val FIND_PRODUCT_PATH = "/product/find/"
    const val FIND_PRODUCT_URL = "$BASE_URL$FIND_PRODUCT_PATH%d"

    private const val FIND_PRODUCTS_PATH = "/products"
    const val FIND_PRODUCTS_URL = "$BASE_URL$FIND_PRODUCTS_PATH"

    private const val FIND_PRODUCT_PAGE_PATH = "/products/paging"
    const val FIND_PRODUCT_PAGE_URL = "$BASE_URL$FIND_PRODUCT_PAGE_PATH"
}
