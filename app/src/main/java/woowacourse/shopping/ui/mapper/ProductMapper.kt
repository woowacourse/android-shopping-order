package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.ProductUiModel

fun ProductUiModel.toDomain(): Product =
    Product(id = id, name = name, price = price.toDomain(), imageUrl = imageUrl)

fun Product.toUi(): ProductUiModel =
    ProductUiModel(id = id, name = name, price = price.toUi(), imageUrl = imageUrl)
