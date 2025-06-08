package woowacourse.shopping.domain.order

@JvmInline
value class ShippingFee(
    val amount: Int = 3000,
) {
    init {
        require(amount >= 0) { "배송비는 음수일 수 없습니다." }
    }
}
