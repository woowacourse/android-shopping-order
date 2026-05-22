package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

interface OrderRepository {
    suspend fun order(orderInfo: OrderInfo)
}
