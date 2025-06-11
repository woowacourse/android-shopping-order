package woowacourse.shopping.mapper

import woowacourse.shopping.data.model.ViewedItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

fun ViewedItem.toUiModel() =
    ProductUiModel(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        category = this.category,
    )

fun ViewedItem.toDomain() =
    Product(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        quantity = 0,
        category = this.category,
    )
