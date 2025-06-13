package woowacourse.shopping.data.remote.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<Long>,
)
