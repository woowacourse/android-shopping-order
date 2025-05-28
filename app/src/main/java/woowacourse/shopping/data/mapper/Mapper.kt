package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.db.CartEntity
import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.response.CartItemContent
import woowacourse.shopping.data.model.response.ProductContent
import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun CartEntity.toProduct() = Product(id, name, imageUrl, Price(price))

fun CartEntity.toCartItem() = CartItem(toProduct(), amount)

fun CartItem.toCartEntity() = CartEntity(product.id, product.name, product.imageUrl, product.price.value, amount)

fun RecentProductEntity.toProduct() =
    Product(
        id = productId,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun Product.toRecentEntity() =
    RecentProductEntity(
        productId = id,
        name = name,
        imageUrl = imageUrl,
        price = price.value,
    )

fun ProductContent.toProduct() =
    Product(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun CartItemContent.toCartItem() =
    CartItem(
        product =
            Product(
                id = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                price = Price(product.price),
            ),
        amount = quantity,
    )

fun ProductResponse.toProduct() =
    Product(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )
