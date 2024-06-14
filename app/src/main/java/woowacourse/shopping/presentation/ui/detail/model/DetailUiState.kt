package woowacourse.shopping.presentation.ui.detail.model

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct

data class DetailUiState(
    val cartProduct: CartProduct? = null,
    val recentProduct: RecentProduct? = null,
    val isNewCartProduct: Boolean = true,
    val isLast: Boolean = false,
    val isLoading: Boolean = true,
)
