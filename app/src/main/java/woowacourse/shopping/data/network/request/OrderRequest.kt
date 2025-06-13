package woowacourse.shopping.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(val cartItemIds: List<Long>)
