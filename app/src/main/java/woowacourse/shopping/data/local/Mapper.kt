package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.history.HistoryEntity
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

fun HistoryEntity.toDomain(): Cart =
    Cart(
        id = id.toLong(),
        product = Product(id = id, name = name, price = price, imageUrl = imageUrl, category = null),
        quantity = quantity,
    )

fun Cart.toHistoryEntity(): HistoryEntity =
    HistoryEntity(id = product.id, name = product.name, price = product.price, imageUrl = product.imageUrl, quantity = quantity)
