package woowacourse.shopping.domain.model

data class Price(
    val orderPrice: Int = 0,
    val discountPrice: Int = 0,
    val shippingFee: Int = 3_000,
    val totalPrice: Int = 0,
)
