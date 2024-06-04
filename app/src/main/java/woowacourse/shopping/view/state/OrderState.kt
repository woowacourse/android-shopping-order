package woowacourse.shopping.view.state

sealed class OrderState {
    data object Cart : OrderState()

    data object Recommend : OrderState()
}
