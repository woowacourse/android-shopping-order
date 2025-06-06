package woowacourse.shopping.data.carts

sealed class CartUpdateResult<out T> {
    data class Success<T>(
        val data: T,
    ) : CartUpdateResult<T>()

    data class Error(
        val error: CartUpdateError,
    ) : CartUpdateResult<Nothing>()
}
