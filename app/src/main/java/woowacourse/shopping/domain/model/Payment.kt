package woowacourse.shopping.domain.model

data class Payment(
    val originPayment: Int,
    val couponDiscount: Int,
    val deliveryFee: Int,
    val totalPayment: Int,
) {
    constructor(originPayment: Int, deliveryFee: Int) : this(originPayment, 0, deliveryFee, originPayment + deliveryFee)
}
