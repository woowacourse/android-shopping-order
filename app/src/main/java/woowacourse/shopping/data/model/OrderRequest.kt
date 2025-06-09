package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<Long>,
)
