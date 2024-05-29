package woowacourse.shopping.remote.api

class ApiClient {
    object Product {
        private const val BASE_URL = "/products"
        const val GET_PRODUCTS = BASE_URL
        const val GET_PRODUCTS_BY_ID = "$BASE_URL/{id}"
    }

    object Cart {
        private const val BASE_URL = "/cart-items"
        const val GET_CART_ITEMS = BASE_URL
        const val POST_CART_ITEM = BASE_URL
        const val DELETE_CART_ITEM = "$BASE_URL/{id}"
        const val PATCH_CART_ITEMS = "$BASE_URL/{id}"
        const val GET_CART_ITEMS_COUNT = "$BASE_URL/counts"
    }
}
