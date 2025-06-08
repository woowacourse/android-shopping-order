package woowacourse.shopping.data.payment

sealed class OrderRequestResult<out T> {
    data class Success<T>(
        val data: T,
    ) : OrderRequestResult<T>()

    data class Error(
        val error: OrderRequestError,
    ) : OrderRequestResult<Nothing>()
}
