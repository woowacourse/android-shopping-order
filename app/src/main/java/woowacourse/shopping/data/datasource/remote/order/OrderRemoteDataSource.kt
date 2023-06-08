package woowacourse.shopping.data.datasource.remote.order

import com.example.domain.model.order.Order
import com.example.domain.model.order.OrderDetailProduct
import com.example.domain.model.order.OrderHistoryInfo
import com.example.domain.model.point.Point

interface OrderRemoteDataSource {

    fun addOrder(
        usedPoint: Point,
        orderDetailProduct: List<OrderDetailProduct>,
        callback: (Result<Unit>) -> Unit
    )

    fun cancelOrder(
        orderId: Int,
        callback: (Result<Unit>) -> Unit
    )

    fun loadAll(
        pageNum: Int,
        callback: (Result<OrderHistoryInfo>) -> Unit
    )

    fun loadDetail(
        detailId: Int,
        callback: (Result<Order>) -> Unit
    )
}
