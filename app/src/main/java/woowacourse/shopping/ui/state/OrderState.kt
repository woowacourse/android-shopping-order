package woowacourse.shopping.ui.state

sealed interface OrderState {
    data object Cart : OrderState

    data object Recommend : OrderState
}
