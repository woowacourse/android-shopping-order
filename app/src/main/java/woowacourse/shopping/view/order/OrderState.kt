package woowacourse.shopping.view.order

data class OrderState(
    val totalPrice: Int,
    val discountPrice: Int,
    val shippingFee: Int,
    val finalPrice: Int,
)
