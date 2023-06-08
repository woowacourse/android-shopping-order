package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO
import woowacourse.shopping.data.remote.result.DataResult

interface OrderDataSource {
    fun getAll(callback: (DataResult<OrdersDTO>) -> Unit)
    fun getOrder(id: Int, callback: (DataResult<OrdersDTO.OrderDTO>) -> Unit)
    fun order(cartProducts: OrderCartItemsDTO, callback: (DataResult<Int?>) -> Unit)
}
