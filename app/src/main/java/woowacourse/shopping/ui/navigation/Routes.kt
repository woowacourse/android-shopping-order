package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Shopping

@Serializable
data class ProductDetail(
    val id: Long,
    val isFromBanner: Boolean = false
)

@Serializable
data object Cart

@Serializable
data class Payment(
    val ids: List<Long>
)

@Serializable
data object Settings
