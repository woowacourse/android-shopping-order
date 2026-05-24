package woowacourse.shopping.ui.pay

sealed interface PayEvent {
    data object NavigateToShopping : PayEvent

    data object CompletePayFailure : PayEvent
}
