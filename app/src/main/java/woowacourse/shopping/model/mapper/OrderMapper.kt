package woowacourse.shopping.model.mapper

import com.example.domain.order.Order
import com.example.domain.order.OrderProduct
import woowacourse.shopping.model.ProductState
import woowacourse.shopping.model.order.OrderProductState
import woowacourse.shopping.model.order.OrderState

fun Order.toUi(): OrderState {
    return OrderState(
        id = id,
        originalPrice = originalPrice,
        discountPrice = discountPrice,
        finalPrice = finalPrice,
        orderDate = orderDate,
        orderProducts = orderProducts.map { it.toUi() }
    )
}

fun OrderProduct.toUi(): OrderProductState {
    return OrderProductState(
        id = id, quantity = quantity,
        product = ProductState(
            id = product.id,
            imageUrl = product.imageUrl,
            name = product.name,
            price = product.price
        )
    )
}
