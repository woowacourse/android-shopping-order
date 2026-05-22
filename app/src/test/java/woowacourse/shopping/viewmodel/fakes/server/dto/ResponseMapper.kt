package woowacourse.shopping.viewmodel.fakes.server.dto

import woowacourse.shopping.domain.Product

fun MockProductResponse.toObject() =
    Product(
        id = id,
        imageUri = imageUri,
        name = name,
        price = price,
        category = category,
    )
