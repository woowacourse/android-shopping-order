package woowacourse.shopping.data.model.remote

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val category: String,
    val imageUrl: String,
)
