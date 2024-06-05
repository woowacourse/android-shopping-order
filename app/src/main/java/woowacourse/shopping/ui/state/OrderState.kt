package woowacourse.shopping.ui.state

sealed class OrderState {
    data object Cart : OrderState()

    data object Recommend : OrderState()
}
