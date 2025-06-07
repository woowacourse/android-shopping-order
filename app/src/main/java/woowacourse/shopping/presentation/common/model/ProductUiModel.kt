package woowacourse.shopping.presentation.common.model

import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

data class ProductUiModel(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val category: String,
)

fun Product.toUiModel() =
    ProductUiModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price.value,
        category = category,
    )

fun ProductUiModel.toProduct() =
    Product(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
        category = category,
    )
