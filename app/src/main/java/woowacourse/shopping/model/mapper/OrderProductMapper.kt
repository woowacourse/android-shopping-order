package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.OrderProduct
import woowacourse.shopping.model.OrderProductModel

fun OrderProductModel.toDomain(): OrderProduct = OrderProduct(
    cartProductId = cartProductId,
    name = name,
    price = price.toDomain(),
    quantity = quantity.toDomain(),
    imageUrl = imageUrl,
)

fun OrderProduct.toUi(): OrderProductModel = OrderProductModel(
    cartProductId = cartProductId,
    name = name,
    price = price.toUi(),
    totalPrice = totalPrice.toUi(),
    quantity = quantity.toUi(),
    imageUrl = imageUrl,
)

fun List<OrderProductModel>.toDomain(): List<OrderProduct> = map { it.toDomain() }
fun List<OrderProduct>.toUi(): List<OrderProductModel> = map { it.toUi() }
