package woowacourse.shopping.data.dto

typealias PaymentDto = Payment

class Payment(
    val originalPayment: Int,
    val finalPayment: Int,
    val point: Int,
)
