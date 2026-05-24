package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Shopping

@Serializable
data class Detail(
    val productId: Long,
)

@Serializable
data object CartGraph

@Serializable
data object Cart

@Serializable
data object Recommend
