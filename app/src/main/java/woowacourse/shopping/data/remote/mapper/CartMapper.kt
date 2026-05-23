package woowacourse.shopping.data.remote.mapper

import woowacourse.shopping.data.remote.dto.response.cart.CartDto
import woowacourse.shopping.data.remote.dto.response.cart.CartProductDto
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

fun CartDto.toDomain(): CartItem =
    CartItem(
        id = id.toString(),
        product = product.toDomain(),
        quantity = quantity,
    )

fun CartProductDto.toDomain(): Product =
    Product(
        id = id.toString(),
        name = ProductName(name),
        price = Money(price),
        imageUrl = imageUrl,
        category = category,
    )
