package woowacourse.shopping.mapper

import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProductUIModel

fun CartProduct.toUIModel(checked: Boolean): CartProductUIModel {
    return CartProductUIModel(
        id = this.id,
        name = this.product.name,
        count = this.quantity,
        checked = checked,
        price = this.product.price,
        imageUrl = this.product.imageUrl,
        productId = this.product.id
    )
}
