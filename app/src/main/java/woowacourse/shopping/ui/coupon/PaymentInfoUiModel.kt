package woowacourse.shopping.ui.coupon

data class PaymentInfoUiModel(
    val orderPrice: Int,
    val discountPrice: Int,
    val shipping: Int,
    val totalPrice: Int
)
