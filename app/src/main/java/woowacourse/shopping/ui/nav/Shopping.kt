package woowacourse.shopping.ui.nav

import kotlinx.serialization.Serializable

@Serializable
object Shopping

@Serializable
data class Detail(
    val productId: String,
    val hideRecentItem: Boolean = false,
)

@Serializable
object Cart

@Serializable
data class Payment(
    val selectedCartItemIds: List<String>,
)

@Serializable
object Setting
