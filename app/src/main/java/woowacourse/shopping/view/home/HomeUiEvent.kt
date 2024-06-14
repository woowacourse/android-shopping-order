package woowacourse.shopping.view.home

sealed interface HomeUiEvent {
    data class NavigateToDetail(val productId: Int) : HomeUiEvent

    data object NavigateToCart : HomeUiEvent

    data object Error : HomeUiEvent
}
