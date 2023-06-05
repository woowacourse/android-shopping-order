package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.domain.model.Order

interface OrderRepository {
    fun getAll(callback: (DataResult<List<Order>>) -> Unit)
    fun getOrder(id: Int, callback: (DataResult<Order>) -> Unit)
    fun order(cartProducts: OrderCartItemsDTO, callback: (DataResult<Int?>) -> Unit)
}
