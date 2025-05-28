package woowacourse.shopping.domain.model

data class Product(
    val productId: Long,
    val name: String,
    private val _price: Price,
    val imageUrl: String,
    val category: String,
) {
    val price get() = _price.value
}
