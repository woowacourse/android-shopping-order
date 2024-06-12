package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import woowacourse.shopping.presentation.model.CartsWrapper

sealed interface OrderRecommendNavigateAction {
    data class NavigateToPayment(val cartsWrapper: CartsWrapper) : OrderRecommendNavigateAction
}
