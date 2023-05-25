package woowacourse.shopping.model

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun CartProduct.toUiModel(isChecked: Boolean = false): CartProductModel =
    CartProductModel(
        isChecked,
        cartId,
        product.id,
        product.name,
        product.imageUrl,
        count,
        product.price.price,
    )

fun CartProductModel.toDomain(): CartProduct =
    CartProduct(cartId, count, Product(id, name, imageUrl, Price(price)))
