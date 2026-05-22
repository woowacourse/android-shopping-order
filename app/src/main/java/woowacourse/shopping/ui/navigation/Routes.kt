package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object ProductListRoute

@Serializable
data class ProductDetailRoute(val productId: Int)

@Serializable
object CartRoute

