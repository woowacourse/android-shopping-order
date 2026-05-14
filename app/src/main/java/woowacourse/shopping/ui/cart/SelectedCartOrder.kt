package woowacourse.shopping.ui.cart

data class SelectedCartOrder(
    val items: List<SelectedCartOrderItem>,
)

data class SelectedCartOrderItem(
    val cartItemId: Long,
    val productId: Long,
    val price: Int,
    val quantity: Int,
)
