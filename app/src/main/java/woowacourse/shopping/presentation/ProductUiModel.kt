package woowacourse.shopping.presentation

data class ProductUiModel(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String = "",
)
