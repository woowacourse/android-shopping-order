package woowacourse.shopping.presentation.ui.curation.model

import woowacourse.shopping.domain.CartProduct

data class CurationUiState(
    val cartProducts: List<CartProduct> = emptyList(),
    val isLoading: Boolean = true,
)
