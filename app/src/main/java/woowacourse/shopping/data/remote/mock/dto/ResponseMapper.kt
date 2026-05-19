package woowacourse.shopping.data.remote.mock.dto

import woowacourse.shopping.domain.Product

fun WebServerResponse.toObject() =
    Product(
        id = id,
        imageUri = imageUri,
        name = name,
        price = price,
        category = category,
    )
