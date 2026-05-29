package woowacourse.shopping.domain.model.order

const val DEFAULT_DELIVERY_FEE = 3_000L

data class OrderPriceSummary(
    val orderAmount: Long,
    val couponDiscount: Long,
    val deliveryFee: Long,
    val totalPaymentPrice: Long,
)
