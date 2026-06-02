package woowacourse.shopping.ui.uimodel

import woowacourse.shopping.core.formatter.toPriceString
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.Products

data class ProductUiModel(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val formattedPrice: String,
    val category: String,
)

fun Product.toProductUiModel(): ProductUiModel =
    ProductUiModel(
        id = id,
        imageUrl = imageUri,
        name = name,
        price = price,
        formattedPrice = price.toPriceString(),
        category = category,
    )

fun Products.toProductUiModel(): List<ProductUiModel> = products.map { it.toProductUiModel() }
