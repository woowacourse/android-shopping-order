package woowacourse.shopping.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<Long>
)