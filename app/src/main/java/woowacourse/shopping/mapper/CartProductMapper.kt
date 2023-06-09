package woowacourse.shopping.mapper

import woowacourse.shopping.data.dto.CartProductDto
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.uimodel.CartProductUIModel

fun CartProduct.toUIModel(isChecked: Boolean): CartProductUIModel {
    return CartProductUIModel(
        id = this.id,
        name = this.product.name,
        count = this.quantity,
        checked = isChecked,
        price = this.product.price,
        imageUrl = this.product.imageUrl,
        productId = this.product.id,
    )
}

fun CartProductDto.toDomain() = CartProduct(
    id = id,
    product = product.toDomain(),
    quantity = quantity,
)
