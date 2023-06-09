package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<Int>,
    val usedPoints: Int,
)
