package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object ProductListRoute

@Serializable
data class ProductDetailRoute(
    val productId: Int,
)

@Serializable
object CartRoute

@Serializable
data class PaymentRoute(
    val selectedItemIds: List<Int> = emptyList(),
)

@Serializable
object SettingsRoute
