package woowacourse.shopping.ui.products.uimodel

import woowacourse.shopping.domain.model.Product

sealed interface ProductUiModel

data class ProductWithQuantityUiModel(
    val product: Product,
    val quantity: Int,
    val isLoading: Boolean = true,
) : ProductUiModel

data object LoadingUiModel : ProductUiModel
