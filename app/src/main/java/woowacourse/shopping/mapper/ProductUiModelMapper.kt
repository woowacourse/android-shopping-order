package woowacourse.shopping.mapper

import woowacourse.shopping.data.model.ViewedItem
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

fun ProductUiModel.toViewedItem() =
    ViewedItem(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
    )
