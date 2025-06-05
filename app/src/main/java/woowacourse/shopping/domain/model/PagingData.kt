package woowacourse.shopping.domain.model

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

data class PagingData(
    val products: List<ProductUiModel>,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)
