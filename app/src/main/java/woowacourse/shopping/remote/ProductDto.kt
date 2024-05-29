package woowacourse.shopping.remote

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
