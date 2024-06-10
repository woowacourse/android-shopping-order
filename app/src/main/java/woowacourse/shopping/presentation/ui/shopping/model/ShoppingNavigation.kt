package woowacourse.shopping.presentation.ui.shopping.model

import woowacourse.shopping.domain.CartProduct

sealed interface ShoppingNavigation {
    data class ToDetail(val cartProduct: CartProduct) : ShoppingNavigation

    data object ToCart : ShoppingNavigation
}
