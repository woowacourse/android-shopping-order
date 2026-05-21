package woowacourse.shopping.navigation

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
