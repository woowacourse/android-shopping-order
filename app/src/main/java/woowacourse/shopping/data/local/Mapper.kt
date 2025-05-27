package woowacourse.shopping.data.local

import woowacourse.shopping.data.local.cart.CartEntity
import woowacourse.shopping.data.local.history.HistoryEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Goods

fun Cart.toEntity(): CartEntity =
    CartEntity(id = product.id, name = product.name, price = product.price, imageUrl = product.imageUrl, quantity = quantity)

fun CartEntity.toDomain(): Cart = Cart(product = Product(id = id, name = name, price = price, imageUrl = imageUrl, category = null), quantity = quantity)

fun HistoryEntity.toDomain(): Cart =
    Cart(product = Product(id = id, name = name, price = price, imageUrl = imageUrl, category = null), quantity = quantity)

fun Cart.toHistoryEntity(): HistoryEntity =
    HistoryEntity(id = product.id, name = product.name, price = product.price, imageUrl = product.imageUrl, quantity = quantity)
