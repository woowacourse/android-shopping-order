package woowacourse.shopping.ui.coupon.uimodel

data class PaymentInfoUiModel(
    val orderPrice: Int,
    val discountPrice: Int,
    val shipping: Int,
    val totalPrice: Int,
)
