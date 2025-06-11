package woowacourse.shopping.domain.model

data class OrderAdjustment(
    val discount: Long = INITIAL_DISCOUNT_CHARGE,
    val deliveryCharge: Long = INITIAL_DELIVERY_CHARGE,
) {
    companion object {
        private const val INITIAL_DELIVERY_CHARGE = 3_000L
        private const val INITIAL_DISCOUNT_CHARGE = 0L
    }
}
