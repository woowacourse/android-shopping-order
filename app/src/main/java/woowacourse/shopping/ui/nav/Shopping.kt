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
