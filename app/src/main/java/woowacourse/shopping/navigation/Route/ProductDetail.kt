package woowacourse.shopping.navigation.Route

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetail (
    val productId: Long,
    val showLastViewed: Boolean
)
