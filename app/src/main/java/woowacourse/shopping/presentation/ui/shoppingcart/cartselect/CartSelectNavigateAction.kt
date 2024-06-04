package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import woowacourse.shopping.domain.model.Cart

sealed interface CartSelectNavigateAction {
    data class NavigateToRecommend(val orderCartProducts: List<Cart>) : CartSelectNavigateAction
}
