package woowacourse.shopping.data.carts

sealed class CartUpdateError {
    data class Server(
        val code: Int,
        val message: String,
    ) : CartUpdateError()

    object Network : CartUpdateError()

    object NotFound : CartUpdateError()
}
