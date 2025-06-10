package woowacourse.shopping.presentation.common.model

import woowacourse.shopping.domain.model.Product

data class SuggestionProductUiModel(
    val productId: Long,
    val imageUrl: String,
    val productName: String,
    val price: Int,
    var quantity: Int = 0,
)

fun Product.toSuggestionUiModel(quantity: Int) =
    SuggestionProductUiModel(
        productId = id,
        productName = name,
        imageUrl = imageUrl,
        price = price.value,
        quantity = quantity,
    )
