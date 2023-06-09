package woowacourse.shopping.presentation.model

data class OrderUiModel(
    val name: String,
    val image: String,
    val otherCount: Int,
    val dateTime: String,
    val price: Int,
)
