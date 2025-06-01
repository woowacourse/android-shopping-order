package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.response.CartItemContent
import woowacourse.shopping.data.model.response.ProductContent
import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun RecentProductEntity.toProduct() =
    Product(
        id = productId,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun Product.toRecentEntity() =
    RecentProductEntity(
        productId = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = price.value,
    )

fun ProductContent.toProduct() =
    Product(
        id = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun CartItemContent.toCartItem() =
    CartItem(
        cartId = id,
        product =
            Product(
                id = product.id,
                category = product.category,
                name = product.name,
                imageUrl = product.imageUrl,
                price = Price(product.price),
            ),
        quantity = quantity,
    )

fun ProductResponse.toProduct() =
    Product(
        id = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )
