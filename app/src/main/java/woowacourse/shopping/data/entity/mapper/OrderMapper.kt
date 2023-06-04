package woowacourse.shopping.data.entity.mapper

import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.data.entity.OrderProductEntity
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL

object OrderMapper : Mapper<Order, OrderEntity> {
    override fun Order.toEntity(): OrderEntity {
        return OrderEntity(
            orderItems = products.map {
                OrderProductEntity(
                    name = it.product.title,
                    imageUrl = it.product.picture.value,
                    count = it.quantity,
                    price = it.product.price
                )},
            originalPrice,
            usedPoints,
            orderPrice = finalPrice
        )
    }

    override fun OrderEntity.toDomain(): Order {
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