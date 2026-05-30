package woowacourse.shopping.presentation.navigation

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.OrderItem

@Serializable
object ShoppingScreen

@Serializable
data class DetailScreen(
    val productId: Long,
    val isFromLastSeen: Boolean = false,
)

@Serializable
object CartScreen

@Serializable
data class RecommendScreen(
    val productIds: List<Long>,
)

@Serializable
data class PaymentScreen(
    val orderItems: List<OrderItem>,
    val orderAmount: Long,
)

@Serializable
object SettingScreen
