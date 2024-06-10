package woowacourse.shopping.presentation.ui.shopping.model

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.common.UiState

data class ShoppingUiState(
    val cartProducts: List<CartProduct> = emptyList(),
    val recentProduct: List<RecentProduct> = emptyList(),
    val pageOffset: Int = -1,
    val isPageEnd: Boolean = false,
    val cartTotalCount: Int = 0,
    val isLoading: Boolean = true
)