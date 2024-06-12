package woowacourse.shopping.domain.mapper

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.presentation.model.ProductUiModel

fun Product.toPresentation(): ProductUiModel {
    return ProductUiModel(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        category = this.category,
    )
}
