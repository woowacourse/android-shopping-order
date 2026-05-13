package woowacourse.shopping.backend.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderInfo(
    val cartItemIds: List<Long>,
)
