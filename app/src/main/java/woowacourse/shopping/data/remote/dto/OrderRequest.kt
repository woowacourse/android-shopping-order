package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val cartItemIds: List<Long>
)