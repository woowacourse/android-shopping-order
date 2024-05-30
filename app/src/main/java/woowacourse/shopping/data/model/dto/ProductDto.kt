package woowacourse.shopping.data.model.dto

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val category: String,
)
