package woowacourse.shopping.model

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun CartProduct.toUiModel(isChecked: Boolean = false): CartProductModel =
    CartProductModel(
        isChecked,
        id,
        product.id,
        product.name,
        product.imageUrl,
        quantity,
        product.price.price,
    )

fun CartProductModel.toDomain(): CartProduct =
    CartProduct(cartId, quantity, Product(id, name, Price(price), imageUrl))
