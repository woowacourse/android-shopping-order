package woowacourse.shopping.util

import woowacourse.shopping.data.remote.cart.CartResponse.Content.CartRemoteProduct
import woowacourse.shopping.data.remote.product.ProductResponse.Content
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
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
        id = id.toLong(),
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
        id = id.toInt(),
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )

fun Content.toDomain(): Product =
    Product(
        id = id.toInt(),
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )
