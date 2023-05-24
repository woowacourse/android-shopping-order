package woowacourse.shopping.model

import woowacourse.shopping.domain.model.Product

fun Product.toUiModel(cartId: Int = 0, count: Int = 0) =
    ProductModel(cartId, id, name, imageUrl, price.price, count)
