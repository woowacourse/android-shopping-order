package woowacourse.shopping

import kotlinx.serialization.Serializable

@Serializable
object ShoppingList

@Serializable
data class ProductDetail(
    val productId: Long,
)

@Serializable
object CartItemList

@Serializable
data class RecommendItem(
    val productIds: List<Long>,
)

@Serializable
data class OrderItem(
    val productIds: List<Long>,
)

@Serializable
object NotificationSetting
