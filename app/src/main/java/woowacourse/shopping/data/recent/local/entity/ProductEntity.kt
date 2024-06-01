package woowacourse.shopping.data.recent.local.entity

data class ProductEntity(
    val productId: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
