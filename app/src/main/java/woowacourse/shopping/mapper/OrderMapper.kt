package woowacourse.shopping.mapper

import com.example.domain.model.Order
import woowacourse.shopping.data.remote.request.OrderDTO
import woowacourse.shopping.model.OrderUIModel

fun Order.toUIModel(): OrderUIModel {
    return OrderUIModel(
        id = this.id,
        orderProducts = this.orderProducts.map { it.toUIModel() },
        timestamp = this.timestamp,
        couponName = this.couponName,
        originPrice = this.originPrice,
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}

fun OrderUIModel.toDomain(): Order {
    return Order(
        id = this.id,
        orderProducts = this.orderProducts.map { it.toDomain() },
        timestamp = this.timestamp,
        couponName = this.couponName,
        originPrice = this.originPrice,
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}

fun OrderDTO.toDomain(): Order {
    return Order(
        id = this.id,
        orderProducts = this.orderProducts.map { it.toDomain() },
        timestamp = this.timestamp,
        couponName = this.couponName,
        originPrice = this.originPrice,
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}
