package woowacourse.shopping.ui.cart

import kotlinx.serialization.Serializable

@Serializable
data class SelectedCartOrder(
    val items: List<SelectedCartOrderItem>,
)

@Serializable
data class SelectedCartOrderItem(
    val cartItemId: Long,
    val productId: Long,
    val price: Int,
    val quantity: Int,
)
