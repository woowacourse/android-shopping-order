package woowacourse.shopping.ui.cart

sealed interface CartEvent {
    data object DeleteCartItemFailure : CartEvent
    data object UpdateCartItemFailure : CartEvent
    data object NavigateToRecommend : CartEvent
}