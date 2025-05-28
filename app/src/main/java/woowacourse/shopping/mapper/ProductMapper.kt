package woowacourse.shopping.mapper

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

fun Product.toUiModel() =
    ProductUiModel(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        quantity = 0,
    )
