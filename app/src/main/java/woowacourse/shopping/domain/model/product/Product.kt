package woowacourse.shopping.domain.model.product

data class Product(
    val id: Long = 0,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val category: String,
)
