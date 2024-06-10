package woowacourse.shopping.view

sealed interface UiState {
    data object Loading : UiState
    data object Init : UiState
}
