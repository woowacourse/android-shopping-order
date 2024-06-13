package woowacourse.shopping.domain.model

data class Product(
    val category: String,
    val productId: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
