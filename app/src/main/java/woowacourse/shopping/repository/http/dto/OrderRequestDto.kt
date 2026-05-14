package woowacourse.shopping.repository.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    val cartItemIds: List<Long>,
)
