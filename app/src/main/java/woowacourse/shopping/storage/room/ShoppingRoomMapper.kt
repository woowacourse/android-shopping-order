package woowacourse.shopping.storage.room

import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductTitle
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemEntity

fun ShoppingItemEntity.toDomain(): ShoppingItem =
    ShoppingItem(
        product =
            Product(
                id = productId,
                title = ProductTitle(title),
                price = Price(price),
                imageUrl = imageUrl,
            ),
        quantity = quantity,
    )

fun ShoppingItem.toEntity(): ShoppingItemEntity =
    ShoppingItemEntity(
        productId = getProductId(),
        title = getProduct().getTitle(),
        price = getProduct().getPrice(),
        imageUrl = getProduct().imageUrl,
        quantity = getQuantity(),
    )

fun ShoppingCartItemRow.toDomain(): ShoppingCartItem =
    ShoppingCartItem(
        id = id,
        shoppingItem =
            ShoppingItem(
                product =
                    Product(
                        id = productId,
                        title = ProductTitle(title),
                        price = Price(price),
                        imageUrl = imageUrl,
                    ),
                quantity = quantity,
            ),
    )

