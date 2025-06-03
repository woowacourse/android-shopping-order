package woowacourse.shopping.util

import woowacourse.shopping.data.remote.cart.CartResponse.Content.CartRemoteProduct
import woowacourse.shopping.data.remote.product.ProductResponse.Content
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

fun List<Any>.replaceCartByProductId(newCart: Cart): List<Any> =
    map {
        if (it is Cart && it.product.id == newCart.product.id) {
            newCart
        } else {
            it
        }
    }

fun Cart.updateQuantity(newQuantity: Int): Cart = this.copy(quantity = newQuantity)

fun CartRemoteProduct.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )

fun Content.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )
