package woowacourse.shopping.data.carts

sealed class CartFetchError {
    data class Server(
        val code: Int,
        val message: String,
    ) : CartFetchError()

    object Network : CartFetchError()

    object Local : CartFetchError()
}
