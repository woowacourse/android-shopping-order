package woowacourse.shopping.data.repository.order

import com.example.domain.datasource.orderDataSource
import com.example.domain.model.Order
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderProduct
import com.example.domain.model.OrderStatus
import com.example.domain.model.Price
import com.example.domain.model.Product
import com.example.domain.repository.OrderRepository

class OrderRepositoryImpl : OrderRepository {
    override fun getOrders(page: Int, onSuccess: (List<Order>) -> Unit, onFailure: () -> Unit) {
        val fromIndex = (page - 1) * 10
        val toIndex = fromIndex + 10
        onSuccess(orderDataSource.subList(fromIndex, toIndex))
    }

    override fun placeOrder(usedPoint: Int, orderProducts: List<OrderProduct>) {

    }

    override fun getOrderDetail(orderId: Int): OrderDetail {
        return OrderDetail(
            orderId = 8642,
            orderAt = "2023-06-03",
            orderStatus = OrderStatus.PENDING,
            payAmount = 5953,
            usedPoint = 9984,
            savedPoint = 9262,
            orderProducts = listOf(
                OrderProduct(
                    quantity = 4859,
                    product = Product(
                        id = 4409,
                        name = "Frankie Cleveland",
                        imgUrl = "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1637154387515l0.jpg",
                        price = Price(value = 9629)
                    )
                ),
                OrderProduct(
                    quantity = 7031,
                    product = Product(
                        id = 4724,
                        name = "Kelli Porter",
                        imgUrl = "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1637154387515l0.jpg",
                        price = Price(value = 9410)
                    )
                ),
                OrderProduct(
                    quantity = 5760,
                    product = Product(
                        id = 9317,
                        name = "Frederic Abbott",
                        imgUrl = "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1637154387515l0.jpg",
                        price = Price(value = 7517)
                    )

                )
            )
        )
    }
}
