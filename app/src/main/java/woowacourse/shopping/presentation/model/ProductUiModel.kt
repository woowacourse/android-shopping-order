package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

data class ProductUiModel(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String = "",
)

fun ProductUiModel.toDomain(): Product =
    Product(
        productId = this.id,
        name = this.name,
        _price = Price(this.price),
        imageUrl = this.imageUrl,
        category = this.category,
    )

fun Product.toPresentation(): ProductUiModel =
    ProductUiModel(
        id = this.productId,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        category = this.category,
    )
