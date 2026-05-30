package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

const val SCHEME = "myapp"

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
data class PaymentRoute(val ids: List<Long>) {
    companion object {
        const val DEEP_LINK = "$SCHEME://payment"
    }
}

@Serializable
data object SettingsRoute
