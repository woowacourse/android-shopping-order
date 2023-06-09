package woowacourse.shopping.model.data.repository

import com.shopping.domain.Order
import com.shopping.domain.OrderDetail
import com.shopping.domain.OrderProduct
import com.shopping.domain.Point
import com.shopping.repository.MemberRepository
import woowacourse.shopping.server.retrofit.MembersService
import woowacourse.shopping.server.retrofit.createResponseCallback

class MemberRepositoryImpl(
    private val service: MembersService
) : MemberRepository {

    override fun getOrderHistories(onSuccess: (List<Order>) -> Unit) {
        service.getOrders().enqueue(
            createResponseCallback(
                onSuccess = { orders ->
                    val orderHistory = orders.map {
                        Order(it.orderId, it.orderPrice, it.totalAmount, it.previewName)
                    }
                    onSuccess(orderHistory)
                },
                onFailure = { throw IllegalStateException("주문 목록을 불러오는데 실패했습니다.") }
            )
        )
    }

    override fun getOrderDetail(id: Long, onSuccess: (OrderDetail) -> Unit) {
        service.getOrder(id).enqueue(
            createResponseCallback(
                onSuccess = { received ->
                    onSuccess(
                        OrderDetail(
                            received.orderItems.map { OrderProduct(it.name, it.imageUrl, it.count, it.price) },
                            received.originalPrice,
                            received.usedPoints,
                            received.orderPrice
                        )
                    )
                },
                onFailure = {
                    throw IllegalStateException("상품 상세를 불러오는데 실패했습니다.")
                }
            )
        )
    }

    override fun getPoint(onSuccess: (Point) -> Unit) {
        service.getPoint().enqueue(
            createResponseCallback(
                onSuccess = {
                    onSuccess(Point(it.point))
                },
                onFailure = {
                    throw IllegalStateException(NOT_FOUNT_POINT_ERROR)
                }
            )
        )
    }

    companion object {
        private const val NOT_FOUNT_POINT_ERROR = "포인트를 불러오는데 실패하였습니다."
    }
}
