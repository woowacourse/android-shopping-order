package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object Shopping

@Serializable
data class ProductDetail(
    val selectedProductId: Long,
    val lastViewedProductId: Long? = null,
)

@Serializable
object Cart

@Serializable
data class Recommendation(
    val totalPrice: Int,
    val checkedIds: List<Long>,
)

@Serializable
data class Payment(
    val checkedIds: List<Long>
)