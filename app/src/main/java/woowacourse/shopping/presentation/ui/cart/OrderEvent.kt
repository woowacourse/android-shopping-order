package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.domain.ProductListItem

sealed interface OrderEvent {
    data class MoveToPayment(val selectedCartItems: List<ProductListItem.ShoppingProductItem>) :
        OrderEvent

    data object MoveToRecommend : OrderEvent
}
