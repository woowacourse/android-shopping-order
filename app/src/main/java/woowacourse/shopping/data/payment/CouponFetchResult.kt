package woowacourse.shopping.data.payment

sealed class CouponFetchResult<out T> {
    data class Success<T>(
        val data: T,
    ) : CouponFetchResult<T>()

    data class Error(
        val error: CouponFetchError,
    ) : CouponFetchResult<Nothing>()
}
