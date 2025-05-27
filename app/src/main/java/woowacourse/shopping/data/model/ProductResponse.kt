package woowacourse.shopping.data.model

data class ProductResponse(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
