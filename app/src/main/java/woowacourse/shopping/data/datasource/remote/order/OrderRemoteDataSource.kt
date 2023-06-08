package woowacourse.shopping.data.datasource.remote.order

import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.OrderHistoryInfo
import com.example.domain.model.OrderInfo
import com.example.domain.model.Point

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
        callback: (Result<OrderInfo>) -> Unit
    )
}
