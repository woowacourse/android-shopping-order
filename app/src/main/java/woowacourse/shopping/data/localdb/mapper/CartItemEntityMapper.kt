package woowacourse.shopping.data.localdb.mapper

import woowacourse.shopping.data.localdb.entity.CartItemEntity
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

fun CartItemEntity.toDomain(): CartItem =
    CartItem(
        id = id,
        product =
            Product(
                id = productId,
                name = ProductName(name),
                price = Money(price),
                imageUrl = imageUrl,
                category = category,
            ),
        quantity = quantity,
    )

fun CartItem.toEntity(timestamp: Long): CartItemEntity =
    CartItemEntity(
        id = id,
        productId = product.id,
        name = product.name.name,
        price = product.price.amount,
        imageUrl = product.imageUrl,
        category = product.category,
        quantity = quantity,
        timestamp = timestamp,
    )
