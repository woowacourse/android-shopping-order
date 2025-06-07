package woowacourse.shopping.data.payment

sealed class CouponFetchError {
    data class Server(
        val code: Int,
        val message: String,
    ) : CouponFetchError()

    object Network : CouponFetchError()
}
