package woowacourse.shopping.mapper

import woowacourse.shopping.data.model.ViewedItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

fun Product.toUiModel() =
    ProductUiModel(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        category = this.category,
        quantity = 0,
    )

fun Product.toViewedItem() =
    ViewedItem(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        category = this.category,
    )
