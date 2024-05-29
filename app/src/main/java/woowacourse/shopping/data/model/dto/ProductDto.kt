package woowacourse.shopping.data.model.dto

data class ProductDto(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
