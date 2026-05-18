package woowacourse.shopping.ui.productList

data class ProductUiModel(
    val id: Int,
    val name: String,
    val price: String,
    val imageUrl: String,
    val cartAmount: String,
    val showAmountController: Boolean,
)
