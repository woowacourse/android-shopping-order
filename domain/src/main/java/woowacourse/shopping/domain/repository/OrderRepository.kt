package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderCartItemsDTO
import woowacourse.shopping.domain.model.OrderDTO
import woowacourse.shopping.domain.model.OrdersDTO

interface OrderRepository {
    fun getAll(callback: (OrdersDTO) -> Unit)
    fun getOrder(id: Int, callback: (OrderDTO) -> Unit)
    fun order(cartProducts: OrderCartItemsDTO, callback: (Boolean) -> Unit)
}
