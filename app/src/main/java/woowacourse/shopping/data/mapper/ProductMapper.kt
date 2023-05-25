package woowacourse.shopping.data.mapper

import woowacourse.shopping.Price
import woowacourse.shopping.Product
import woowacourse.shopping.data.product.ProductDataModel

fun ProductDataModel.toDomain(): Product {
    return Product(
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = Price(price),
    )
}

fun Product.toDataModel(): ProductDataModel {
    return ProductDataModel(
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = price.value,
    )
}
