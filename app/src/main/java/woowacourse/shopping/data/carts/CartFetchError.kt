package woowacourse.shopping.data.carts

sealed class CartFetchError(message: String? = null) : Throwable(message) {
    object Network : CartFetchError("Network Error")
}