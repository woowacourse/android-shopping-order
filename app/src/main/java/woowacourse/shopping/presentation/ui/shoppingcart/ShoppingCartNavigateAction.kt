package woowacourse.shopping.presentation.ui.shoppingcart

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.model.CartsWrapper

sealed interface ShoppingCartNavigateAction {
    fun navigateToOrderRecommend(carts: List<Cart>)

    fun navigateToPayment(cartsWrapper: CartsWrapper)
}
