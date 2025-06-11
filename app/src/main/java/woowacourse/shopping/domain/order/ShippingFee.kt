package woowacourse.shopping.domain.order

@JvmInline
value class ShippingFee(
    val amount: Int = DEFAULT_SHIPPING_FEE,
) {
    init {
        require(amount >= 0) { "배송비는 음수일 수 없습니다." }
    }

    companion object {
        private const val DEFAULT_SHIPPING_FEE = 3_000
    }
}
