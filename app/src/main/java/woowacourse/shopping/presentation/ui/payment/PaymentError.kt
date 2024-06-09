package woowacourse.shopping.presentation.ui.payment

enum class PaymentError(
    val message: String,
) {
    CouponNotFound("쿠폰을 불러올 수 없습니다."),
    CartItemsNotFound("장바구니를 불러올 수 없습니다."),
}
