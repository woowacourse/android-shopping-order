package woowacourse.shopping

import kotlinx.serialization.Serializable

@Serializable
object ProductListRoute

@Serializable
data class ProductDetailRoute(
    val productId: String,
    val recentProductId: String?,
)

@Serializable
object CartRoute

@Serializable
data class RecommendRoute(
    val contentIds: List<String>,
)

@Serializable
data class PurchaseRoute(
    val contentIds: List<String>,
    val totalPrice: Int,
)

@Serializable
object SettingRoute
