package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.model.ProductModel

fun ProductModel.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = price.toDomain(),
        imageUrl = imageUrl,
    )

fun Product.toUi(): ProductModel =
    ProductModel(
        id = id,
        name = name,
        price = price.toUi(),
        imageUrl = imageUrl,
    )
