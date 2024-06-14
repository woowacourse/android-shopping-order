package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import woowacourse.shopping.domain.model.Cart

sealed interface OrderRecommendNavigateAction {
    data class NavigateToPayment(val orderCarts: List<Cart>) : OrderRecommendNavigateAction
}
