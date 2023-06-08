package woowacourse.shopping.presentation.fixture

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderProduct
import woowacourse.shopping.model.Price
import woowacourse.shopping.presentation.model.OrderModel
import woowacourse.shopping.presentation.model.OrderProductModel
import java.time.LocalDateTime

object OrderFixture {
    fun getOrders(vararg ids: Long): List<Order> {
        return ids.map { getOrder(it) }
    }

    fun getOrder(id: Long): Order {
        return Order(
            orderId = id,
            orderDateTime = LocalDateTime.of(2023, 6, 6, 10, 0, 0),
            orderProducts = getOrderProducts(1, 2, 3),
            totalPrice = Price(3000),
        )
    }

    fun getOrderProducts(vararg ids: Long): List<OrderProduct> {
        return ids.map {
            OrderProduct(
                product = CartProductFixture.getProduct(it),
                quantity = 1,
            )
        }
    }

    fun getOrderModels(vararg ids: Long): List<OrderModel> {
        return ids.map {
            getOrderModel(it)
        }
    }

    fun getOrderModel(id: Long): OrderModel {
        return OrderModel(
            orderId = id,
            orderDateTime = LocalDateTime.of(2023, 6, 6, 10, 0, 0),
            orderProductModels = getOrderProductModels(1, 2, 3),
            totalPrice = 3000,
        )
    }

    fun getOrderProductModels(vararg ids: Long): List<OrderProductModel> {
        return ids.map {
            OrderProductModel(
                productModel = CartProductFixture.getProductModel(it),
                quantity = 1,
            )
        }
    }
}
