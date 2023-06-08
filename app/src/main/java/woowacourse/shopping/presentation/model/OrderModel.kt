package woowacourse.shopping.presentation.model

data class OrderModel(
    val id: Int,
    val firstProductName: String,
    val totalCount: Int,
    val imageUrl: String,
    val spendPrice: Int,
    val createAt: String,
)
