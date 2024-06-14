package woowacourse.shopping.data.model

import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.toData

data class OrderItemData(
    val cartItemId: Long,
    val quantity: Int,
    val product: ProductData,
)

fun OrderItem.toData(): OrderItemData =
    OrderItemData(
        cartItemId = cartItemId,
        quantity = quantity,
        product = product.toData(),
    )

fun List<OrderItem>.toData(): List<OrderItemData> = map(OrderItem::toData)

fun OrderItemData.toDomain(): OrderItem =
    OrderItem(
        cartItemId = cartItemId,
        quantity = quantity,
        product = product.toDomain(),
    )

fun List<OrderItemData>.toDomain(): List<OrderItem> = map(OrderItemData::toDomain)
