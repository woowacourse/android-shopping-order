package woowacourse.shopping.ui.nav

import kotlinx.serialization.Serializable

@Serializable
object Shopping

@Serializable
data class ProductDetail(
    val productId: Long,
    val isFromBanner: Boolean = false,
)

@Serializable
object Cart

@Serializable
data class Payment(
    val cartItemIds: String,
)
