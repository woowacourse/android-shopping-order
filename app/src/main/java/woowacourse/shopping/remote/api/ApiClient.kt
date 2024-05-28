package woowacourse.shopping.remote.api

class ApiClient {
    object Product {
        private const val BASE_URL = "/products"
        const val GET_PRODUCTS = BASE_URL
        const val GET_PRODUCTS_BY_ID = "$BASE_URL/{id}"
    }
}
