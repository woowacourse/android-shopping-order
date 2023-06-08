package woowacourse.shopping.model

import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo

fun Product.toUiModel(cartId: Int?, count: Int = 0) =
    ProductModel(cartId, id, name, imageUrl, price.price, count)

fun ProductModel.toDomain() = Product(id, name, Price(price), imageUrl)
fun ProductWithCartInfo.toUiModel(): ProductModel {
    return ProductModel(cartItem?.id, product.id, product.name, product.imageUrl, product.price.price, cartItem?.quantity ?: 0)
}
