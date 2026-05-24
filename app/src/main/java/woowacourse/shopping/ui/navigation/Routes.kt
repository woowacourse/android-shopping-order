package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ShoppingRoute

@Serializable
data class ProductDetailRoute(
    val id: Long,
    val isFromBanner: Boolean = false,
)

@Serializable
data object CartRoute

@Serializable
data class PaymentRoute(
    val ids: List<Long>,
)

@Serializable
data object SettingsRoute
