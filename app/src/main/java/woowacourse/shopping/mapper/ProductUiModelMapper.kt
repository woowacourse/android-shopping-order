package woowacourse.shopping.mapper

import woowacourse.shopping.data.model.ViewedItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

fun ProductUiModel.toViewedItem() =
    ViewedItem(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        category = this.category,
    )

fun ProductUiModel.toDomain() =
    Product(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
        category = this.category,
    )
