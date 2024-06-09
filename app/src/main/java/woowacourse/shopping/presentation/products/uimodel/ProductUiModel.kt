package woowacourse.shopping.presentation.products.uimodel

import com.example.domain.model.Product
import com.example.domain.model.Quantity

data class ProductUiModel(
    val product: Product,
    val quantity: Quantity,
)
