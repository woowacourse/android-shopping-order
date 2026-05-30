package woowacourse.shopping.data.source.remote.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<Long>,
)
