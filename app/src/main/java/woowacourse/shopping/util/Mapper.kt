package woowacourse.shopping.util

import woowacourse.shopping.data.remote.cart.CartResponse.Content.CartRemoteProduct
import woowacourse.shopping.data.remote.product.ProductResponse.Content
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product

fun List<Any>.replaceCartByProductId(newCart: CartProduct): List<Any> =
    map {
        if (it is CartProduct && it.product.id == newCart.product.id) {
            newCart
        } else {
            it
        }
    }

fun CartProduct.updateQuantity(newQuantity: Int): CartProduct = this.copy(quantity = newQuantity)

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
