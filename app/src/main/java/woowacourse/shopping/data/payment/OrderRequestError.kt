package woowacourse.shopping.data.payment

sealed class OrderRequestError {
    data class Server(
        val code: Int,
        val message: String,
    ) : OrderRequestError()

    object Network : OrderRequestError()
}
