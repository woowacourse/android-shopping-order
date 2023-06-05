package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.ProductUiModel

fun ProductUiModel.toDomainModel(): Product =
    Product(id = id, name = name, price = price.toDomainModel(), imageUrl = imageUrl)

fun Product.toUiModel(): ProductUiModel =
    ProductUiModel(id = id, name = name, price = price.toUiModel(), imageUrl = imageUrl)
