package woowacourse.shopping.domain.model

data class Price(
    val orderPrice: Int = 0,
    val totalPrice: Int = 0,
    val shippingFee: Int = 3_000,
)
