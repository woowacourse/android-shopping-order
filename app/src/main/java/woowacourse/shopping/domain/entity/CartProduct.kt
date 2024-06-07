package woowacourse.shopping.domain.entity

data class CartProduct(
    val product: Product,
    val count: Int,
    val id: Long = -1,
) {
    init {
        require(count >= MIN_COUNT_LIMIT) { "카트의 수량은 1개 이상이어야 합니다." }
    }

    val totalPrice get(): Long = product.price.toLong() * count

    fun increaseCount(amount: Int = 1): CartProduct = CartProduct(product, count + amount)

    fun canDecreaseCount(amount: Int = 1): Boolean = count.minus(amount) >= MIN_COUNT_LIMIT

    fun decreaseCount(amount: Int = 1): CartProduct = CartProduct(product, count - amount)

    companion object {
        private const val MIN_COUNT_LIMIT = 1
    }
}
