package woowacourse.shopping.model

typealias UiPayment = Payment

class Payment(
    val originalPayment: Int,
    val finalPayment: Int,
    val point: Int,
)
