package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.member.response.GetOrderResponse
import woowacourse.shopping.data.dto.OrderProduct
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL

object OrderMapper : Mapper<Order, GetOrderResponse> {
    override fun Order.toEntity(): GetOrderResponse {
        return GetOrderResponse(
            orderItems = products.map {
                OrderProduct(
                    name = it.product.title,
                    imageUrl = it.product.picture.value,
                    count = it.quantity,
                    price = it.product.price
                )
            },
            originalPrice,
            usedPoints,
            orderPrice = finalPrice
        )
    }

    override fun GetOrderResponse.toDomain(): Order {
        return Order(
            products = orderItems.map {
                CartProduct(
                    id = 0,
                    quantity = it.count,
                    isChecked = true,
                    product = Product(
                        id = 0, picture = URL(it.imageUrl), title = it.name, price = it.price
                    )
                )},
            originalPrice,
            usedPoints,
            finalPrice = orderPrice
        )
    }
}