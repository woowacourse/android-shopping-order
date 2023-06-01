package woowacourse.shopping.mapper

import woowacourse.shopping.domain.model.OrderProduct
import woowacourse.shopping.model.UiOrderProduct

fun UiOrderProduct.toDomain(): OrderProduct = OrderProduct(
    cartProductId = cartProductId,
    name = name,
    price = price.toDomain(),
    quantity = quantity.toDomain(),
    imageUrl = imageUrl,
)

fun OrderProduct.toUi(): UiOrderProduct = UiOrderProduct(
    cartProductId = cartProductId,
    name = name,
    price = price.toUi(),
    totalPrice = totalPrice.toUi(),
    quantity = quantity.toUi(),
    imageUrl = imageUrl,
)

fun List<UiOrderProduct>.toDomain(): List<OrderProduct> = map { it.toDomain() }
fun List<OrderProduct>.toUi(): List<UiOrderProduct> = map { it.toUi() }
