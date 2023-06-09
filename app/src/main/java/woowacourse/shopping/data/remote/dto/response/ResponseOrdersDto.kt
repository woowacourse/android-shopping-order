package woowacourse.shopping.data.remote.dto.response

import woowacourse.shopping.domain.model.Order

data class ResponseOrdersDto(
    val orders: List<Order>,
)
