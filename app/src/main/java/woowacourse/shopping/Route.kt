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
