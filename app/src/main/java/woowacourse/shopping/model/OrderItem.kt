package woowacourse.shopping.model

data class OrderItem(
    val productId: Long,
    val unitPrice: Price,
    val quantity: Int,
) {
    init {
        require(quantity > 0) { "주문 수량은 1 이상이어야 합니다." }
    }
}
