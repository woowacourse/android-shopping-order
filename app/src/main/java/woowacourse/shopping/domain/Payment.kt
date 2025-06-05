package woowacourse.shopping.domain

data class Payment(
    val originPayment: Int,
    val couponDiscount: Int,
    val deliveryFee: Int,
    val totalPayment: Int,
)
