package woowacourse.shopping.domain

data class Product(
    val id: Long,
    val category: String,
    val name: String,
    val imageUrl: String,
    val price: Price,
)
