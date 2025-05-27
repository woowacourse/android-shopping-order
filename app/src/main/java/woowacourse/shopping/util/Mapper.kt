package woowacourse.shopping.util

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.feature.model.CartUiModel

fun Cart.toUi(): CartUiModel =
    CartUiModel(
        id = product.id,
        name = product.name,
        price = product.price,
        imageUrl = product.imageUrl,
        quantity = quantity,
    )

fun CartUiModel.toDomain(): Cart =
    Cart(
        quantity = quantity,
        product =
            Product(
                id = id,
                name = name,
                price = price,
                imageUrl = imageUrl,
                category = "",
            ),
    )

fun List<Any>.updateCartQuantity(
    id: Int,
    newQuantity: Int,
): List<Any> =
    this.map { item ->
        if (item is Cart && item.product.id == id) {
            item.copy(quantity = newQuantity)
        } else {
            item
        }
    }

fun Cart.updateQuantity(newQuantity: Int): Cart = this.copy(quantity = newQuantity)
