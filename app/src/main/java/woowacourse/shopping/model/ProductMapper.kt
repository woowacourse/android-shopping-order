package woowacourse.shopping.model

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo

fun Product.toUiModel(cartId: Int = 0, count: Int = 0) =
    ProductModel(cartId, id, name, imageUrl, price, count)

fun ProductWithCartInfo.toUiModel(): ProductModel {
    if (cartItem == null) {
        return ProductModel(null, product.id, product.name, product.imageUrl, product.price, 0)
    }
    return ProductModel(cartItem!!.id, product.id, product.name, product.imageUrl, product.price, cartItem!!.quantity)
}
