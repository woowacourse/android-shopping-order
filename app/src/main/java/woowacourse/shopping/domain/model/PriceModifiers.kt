package woowacourse.shopping.domain.model

data class PriceModifiers(
    val orderAmount: Long,
    val discountAmount: Long,
    val deliveryFee: Int,
) {
    val totalAmount: Long
        get() = (orderAmount + deliveryFee - discountAmount).coerceAtLeast(0)
}
