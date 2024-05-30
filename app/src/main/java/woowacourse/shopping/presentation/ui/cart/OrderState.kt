package woowacourse.shopping.presentation.ui.cart

sealed interface OrderState {
    data object Recommend : OrderState

    data object CartList : OrderState
}
