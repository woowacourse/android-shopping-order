package woowacourse.shopping.mapper

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProductUIModel

fun List<CartProduct>.toUIModel(): List<CartProductUIModel> {
    return this.map { it.toUIModel() }
}

fun CartProduct.toUIModel(): CartProductUIModel {
    return CartProductUIModel(
        id = this.id,
        name = this.product.name,
        count = this.quantity,
        checked = this.checked,
        price = this.product.price,
        imageUrl = this.product.imageUrl,
        productId = this.product.id
    )
}
