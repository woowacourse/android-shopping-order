package woowacourse.shopping.mapper

import woowacourse.shopping.model.Product
import woowacourse.shopping.uimodel.ProductUIModel

fun Product.toUIModel(): ProductUIModel {
    return ProductUIModel(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
    )
}

fun ProductUIModel.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
    )
}
