package woowacouse.shopping.model.product

data class Product(
    val id: Long,
    val title: String,
    val price: Int,
    val imageUrl: String,
    val count: Int = 0,
) {
    fun increaseCount(): Product = copy(count = (count + 1).coerceAtMost(MAX_COUNT))
    fun decreaseCount(): Product = copy(count = (count - 1).coerceAtLeast(MIN_COUNT))

    companion object {
        private const val MIN_COUNT = 0
        private const val MAX_COUNT = 99
    }
}
