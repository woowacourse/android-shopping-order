package woowacourse.shopping.data.datasource.remote.orderdetail

import woowacourse.shopping.data.remote.request.OrderDTO

interface OrderDetailSource {
    fun getById(orderId: Long): Result<OrderDTO>
}
