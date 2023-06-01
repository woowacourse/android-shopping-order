package woowacourse.shopping.data.mapper

import woowacourse.shopping.Order
import woowacourse.shopping.OrderDetail
import woowacourse.shopping.OrderProduct
import woowacourse.shopping.OrderProducts
import woowacourse.shopping.Point
import woowacourse.shopping.Price
import woowacourse.shopping.Product
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel
import woowacourse.shopping.data.order.model.OrderProductDataModel
import woowacourse.shopping.util.LocalDateTimeHelper

fun OrderDataModel.toDomain(): Order {
    return Order(
        orderId = orderId,
        imageUrl = imageUrl,
        orderDate = LocalDateTimeHelper.convertStringToLocalDateTime(createdAt),
        spendPrice = Price(value = spendPrice)
    )
}

fun OrderDetailDataModel.toDomain(): OrderDetail {
    return OrderDetail(
        orderId = orderId,
        totalPrice = Price(totalPrice),
        spendPoint = Point(spendPoint),
        spendPrice = Price(spendPrice),
        orderDate = LocalDateTimeHelper.convertStringToLocalDateTime(createdAt),
        orderItems = OrderProducts(orderItems.map { it.toDomain() })
    )
}

fun OrderProductDataModel.toDomain(): OrderProduct {
    return OrderProduct(
        product = Product(
            id = productId,
            imageUrl = imageUrl,
            name = name,
            price = Price(value = price)
        ),
        count = quantity
    )
}
