package woowacourse.shopping.presentation.products.uimodel

import com.example.domain.model.Product

data class RecentProductUiModel(
    val product: Product,
    val imageUrl: String,
    val title: String,
)
