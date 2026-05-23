package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ProductListRoute

@Serializable
data class DetailRoute(
    val productId: Long,
    val showLastViewed: Boolean = true,
)

@Serializable
data object CartRoute

@Serializable
data object RecommendRoute

@Serializable
data object CartGraphRoute
