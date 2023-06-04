package woowacourse.shopping.data.model

data class OrderPostEntity(
    val cartItemIds: List<Long>,
    val cardNumber: String,
    val cvc: Int,
    val point: Int,
)
