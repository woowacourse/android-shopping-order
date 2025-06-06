package woowacourse.shopping.data.carts

sealed class CartFetchResult<out T> {
    data class Success<T>(
        val data: T,
    ) : CartFetchResult<T>()

    data class Error(
        val error: CartFetchError,
    ) : CartFetchResult<Nothing>()
}
