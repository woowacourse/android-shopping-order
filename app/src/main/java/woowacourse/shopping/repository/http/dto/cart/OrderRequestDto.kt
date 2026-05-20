package woowacourse.shopping.repository.http.dto.cart

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    val cartItemIds: List<Long>,
)
