package woowacourse.shopping.view.order

import woowacourse.shopping.domain.exception.NetworkError

sealed interface OrderUiEvent {
    data class ShowErrorMessage(val throwable: NetworkError) : OrderUiEvent

    data object OrderComplete : OrderUiEvent
}
