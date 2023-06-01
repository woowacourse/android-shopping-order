package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Product
import woowacourse.shopping.ui.model.ProductUiModel

fun ProductUiModel.toProductDomainModel(): Product =
    Product(id = id, name = name, price = price.toProductDomainModel(), imageUrl = imageUrl)

fun Product.toProductUiModel(): ProductUiModel =
    ProductUiModel(id = id, name = name, price = price.toPriceUiModel(), imageUrl = imageUrl)
